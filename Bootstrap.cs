using System;
using System.IO;
using System.IO.Compression;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Diagnostics;
using System.Net;
using System.Net.Http;
using System.Security.Cryptography;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Web.Script.Serialization;
using System.Windows.Forms;

namespace ProjectBattleAuto {
public sealed class Release {
 public string version, url, sha256, entry, notes;
 public long size;
}
public sealed class Envelope { public string payload, signature; }
public sealed class Installed { public string version, directory, entry; }
public static class Engine {
 public const string Channel = "https://github.com/vok1si/project-battle-pack-NOW/releases/download/launcher-auto-channel/launcher-channel.json";
 public static string Root = SelectRoot(AppDomain.CurrentDomain.BaseDirectory,
  Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData),"Project Battle Auto"));
 public static string SelectRoot(string launcherDirectory,string userDirectory) {
  string portable=Path.Combine(launcherDirectory,"Project Battle Data");
  try {
   Directory.CreateDirectory(portable);
   string probe=Path.Combine(portable,".write-"+Guid.NewGuid().ToString("N"));
   using(new FileStream(probe,FileMode.CreateNew,FileAccess.Write,FileShare.None,1,FileOptions.DeleteOnClose)) { }
   return portable;
  } catch(UnauthorizedAccessException) { return userDirectory; }
    catch(IOException) { return userDirectory; }
 }
 public static readonly JavaScriptSerializer Json = new JavaScriptSerializer();
 public static string PublicKey;
 public static Release Verify(string body) {
  Envelope e = Json.Deserialize<Envelope>(body);
  byte[] bytes = Convert.FromBase64String(e.payload);
  using(var rsa = new RSACryptoServiceProvider()) {
   rsa.FromXmlString(PublicKey);
   if(!rsa.VerifyData(bytes, CryptoConfig.MapNameToOID("SHA256"), Convert.FromBase64String(e.signature))) throw new IOException("Подпись обновления недействительна.");
  }
  Release r = Json.Deserialize<Release>(Encoding.UTF8.GetString(bytes));
  Version.Parse(r.version);
  Uri uri;
  if(!Uri.TryCreate(r.url, UriKind.Absolute, out uri) || uri.Scheme != "https" || uri.Host != "github.com") throw new IOException("Недопустимый адрес обновления.");
  if(r.size <= 0 || r.size > 2147483648L || r.sha256 == null || !System.Text.RegularExpressions.Regex.IsMatch(r.sha256,"^[0-9a-fA-F]{64}$")) throw new IOException("Некорректный манифест обновления.");
  SafePath(Path.Combine(Root, "versions", "validation"), r.entry);
  return r;
 }
 public static string SafePath(string root, string relative) {
  if(String.IsNullOrWhiteSpace(relative) || Path.IsPathRooted(relative) || relative.Contains(":")) throw new IOException("Недопустимый путь пакета.");
  string prefix = Path.GetFullPath(root).TrimEnd(Path.DirectorySeparatorChar) + Path.DirectorySeparatorChar;
  string result = Path.GetFullPath(Path.Combine(prefix, relative.Replace('/',Path.DirectorySeparatorChar)));
  if(!result.StartsWith(prefix,StringComparison.OrdinalIgnoreCase)) throw new IOException("Путь выходит за пределы пакета.");
  return result;
 }
 public static Installed Current() {
  string p = Path.Combine(Root,"current.json");
  if(!File.Exists(p)) return null;
  Installed state = Json.Deserialize<Installed>(File.ReadAllText(p));
  Version.Parse(state.version);
  string dir = SafePath(Path.Combine(Root,"versions"), state.directory);
  if(!File.Exists(SafePath(dir,state.entry))) throw new IOException("Установленный лаунчер повреждён. Нажмите «Восстановить».");
  return state;
 }
 public static void Extract(string archive,string destination) {
  Directory.CreateDirectory(destination);
  using(var zip = ZipFile.OpenRead(archive)) {
   long expanded = 0;
   foreach(var e in zip.Entries) {
    expanded += e.Length;
    if(expanded > 4294967296L) throw new IOException("Слишком большой размер распакованного пакета.");
    string target = SafePath(destination,e.FullName);
    if(e.FullName.EndsWith("/")) { Directory.CreateDirectory(target); continue; }
    Directory.CreateDirectory(Path.GetDirectoryName(target));
    e.ExtractToFile(target,false);
   }
  }
 }
 public static void ValidateHash(string file,Release release) {
  if(new FileInfo(file).Length != release.size) throw new IOException("Обновление скачано не полностью.");
  using(var sha=SHA256.Create()) using(var stream=File.OpenRead(file)) {
   string actual=BitConverter.ToString(sha.ComputeHash(stream)).Replace("-","");
   if(!actual.Equals(release.sha256,StringComparison.OrdinalIgnoreCase)) throw new IOException("Контрольная сумма обновления не совпадает.");
  }
 }
 public static Installed Commit(string archive, Release release) {
  ValidateHash(archive,release);
  string id=release.version+"-"+Guid.NewGuid().ToString("N");
  string stage=Path.Combine(Root,"versions",id);
  try {
   Extract(archive,stage);
   if(!File.Exists(SafePath(stage,release.entry))) throw new IOException("В пакете отсутствует EXE лаунчера.");
   Installed installed=new Installed {version=release.version,directory=id,entry=release.entry};
   string next=Path.Combine(Root,"current.new.json");
   File.WriteAllText(next,Json.Serialize(installed),Encoding.UTF8);
   string current=Path.Combine(Root,"current.json");
   if(File.Exists(current)) File.Replace(next,current,Path.Combine(Root,"previous.json"),true);
   else File.Move(next,current);
   return installed;
  } catch { if(Directory.Exists(stage)) Directory.Delete(stage,true); throw; }
 }
 public static HttpClient Client() {
  ServicePointManager.SecurityProtocol=SecurityProtocolType.Tls12;
  var client=new HttpClient(); client.Timeout=TimeSpan.FromMinutes(15);
  client.DefaultRequestHeaders.UserAgent.ParseAdd("ProjectBattleAuto/1.0");
  client.DefaultRequestHeaders.CacheControl=new System.Net.Http.Headers.CacheControlHeaderValue {NoCache=true};
  return client;
 }
}
public sealed class GoldProgress:Control {
 int value;
 public int Value {get{return value;}set{this.value=Math.Max(0,Math.Min(100,value));Invalidate();}}
 public GoldProgress(){SetStyle(ControlStyles.UserPaint|ControlStyles.AllPaintingInWmPaint|ControlStyles.OptimizedDoubleBuffer,true);}
 protected override void OnPaint(PaintEventArgs e){e.Graphics.Clear(Color.FromArgb(34,40,35)); if(value>0)using(var b=new LinearGradientBrush(ClientRectangle,Color.FromArgb(225,212,161),Color.FromArgb(150,133,85),0f))e.Graphics.FillRectangle(b,0,0,Width*value/100f,Height);}
}
public sealed class Window:Form {
 readonly Label title=new Label(), status=new Label(), detail=new Label();
 readonly Button update=new Button(), cancel=new Button();
 readonly GoldProgress progress=new GoldProgress();
 readonly CancellationTokenSource stop=new CancellationTokenSource();
 readonly System.Windows.Forms.Timer animation=new System.Windows.Forms.Timer();
 readonly Stopwatch clock=Stopwatch.StartNew();
 readonly Image planet;
 Release release; bool busy;
 public Window():this(false) {}
 public Window(bool preview) {
  Text="Project Battle — обновление лаунчера"; ClientSize=new Size(720,460);
  SetStyle(ControlStyles.UserPaint|ControlStyles.AllPaintingInWmPaint|ControlStyles.OptimizedDoubleBuffer|ControlStyles.ResizeRedraw,true);
  using(var stream=typeof(Program).Assembly.GetManifestResourceStream("planet.png"))using(var img=Image.FromStream(stream))planet=new Bitmap(img);
  using(var stream=typeof(Program).Assembly.GetManifestResourceStream("pb.ico"))Icon=new Icon(stream);
  StartPosition=FormStartPosition.CenterScreen; FormBorderStyle=FormBorderStyle.None;
  MaximizeBox=false; BackColor=Color.FromArgb(13,19,18); ForeColor=Color.FromArgb(229,218,174);
  Font=new Font("Segoe UI",10);
  title.SetBounds(32,42,590,35); title.Text="PROJECT BATTLE"; title.Font=new Font("Segoe UI",19,FontStyle.Bold);
  var subtitle=new Label{Text="ЛАУНЧЕР  /  ОБНОВЛЕНИЯ",ForeColor=Color.FromArgb(137,146,138),Font=new Font("Segoe UI",8)};subtitle.SetBounds(34,80,400,20);Controls.Add(subtitle);
  status.SetBounds(32,276,656,28); status.Text="Проверяем обновление лаунчера…";
  detail.SetBounds(32,310,656,45); detail.ForeColor=Color.FromArgb(155,165,158);
  progress.SetBounds(32,365,656,5);
  update.SetBounds(390,395,144,40); update.Text="Обновить"; update.Visible=false;
  cancel.SetBounds(544,395,144,40); cancel.Text="Отмена";
  foreach(Button button in new[]{update,cancel}) {button.FlatStyle=FlatStyle.Flat;button.BackColor=Color.FromArgb(36,44,39);button.ForeColor=ForeColor;}
  Controls.AddRange(new Control[]{title,status,detail,progress,update,cancel});
  var close=new Button{Text="×",FlatStyle=FlatStyle.Flat,ForeColor=Color.FromArgb(165,171,157),TabStop=false};close.FlatAppearance.BorderSize=0;close.SetBounds(681,5,32,26);close.Click+=(s,e)=>Close();Controls.Add(close);
  var minimize=new Button{Text="−",FlatStyle=FlatStyle.Flat,ForeColor=close.ForeColor,TabStop=false};minimize.FlatAppearance.BorderSize=0;minimize.SetBounds(647,5,32,26);minimize.Click+=(s,e)=>WindowState=FormWindowState.Minimized;Controls.Add(minimize);
  cancel.Click+=(s,e)=>Close(); update.Click+=async(s,e)=>await Install();
  animation.Interval=16; animation.Tick+=(s,e)=>Invalidate(new Rectangle(275,105,170,160));animation.Start();
  FormClosed+=(s,e)=>{animation.Stop();animation.Dispose();planet.Dispose();};
  FormClosing+=(s,e)=>stop.Cancel();
  if(!preview) Shown+=async(s,e)=>await Check();
  else {status.Text="Выпущено обновление лаунчера 0.1.78.1";detail.Text="Оформление, ресурсы и улучшенная загрузка.";update.Visible=true;}
 }
 protected override void WndProc(ref Message m) {base.WndProc(ref m);if(m.Msg==0x84&&(int)m.Result==1){var p=PointToClient(Cursor.Position);if(p.Y<35&&p.X<640)m.Result=(IntPtr)2;}}
 protected override void OnPaint(PaintEventArgs e) {
  base.OnPaint(e);e.Graphics.SmoothingMode=SmoothingMode.AntiAlias;
  using(var line=new Pen(Color.FromArgb(29,36,32))) {e.Graphics.DrawRectangle(line,0,0,Width-1,Height-1);for(int y=108;y<265;y+=3)e.Graphics.DrawLine(line,1,y,Width-2,y);}
  e.Graphics.DrawImage(planet,326,153,68,68);
  {
   for(int i=0;i<3;i++) {
    double a=(i*120-90)*Math.PI/180;
    double pulse=Math.Pow((Math.Cos(clock.Elapsed.TotalSeconds*2*Math.PI/2.4-i*2*Math.PI/3)+1)/2,3);
    float x=360+(float)Math.Cos(a)*62,y=187+(float)Math.Sin(a)*62;
    using(var glow=new SolidBrush(Color.FromArgb((int)(28*pulse),218,198,132)))e.Graphics.FillEllipse(glow,x-10,y-10,20,20);
    int light=100+(int)(110*pulse);
    using(var b=new SolidBrush(Color.FromArgb(light,light-9,Math.Max(0,light-48))))e.Graphics.FillEllipse(b,x-5.5f,y-5.5f,11,11);
   }
  }
 }
 async Task Check() {
  try {
   Directory.CreateDirectory(Engine.Root);
   using(var client=Engine.Client()) using(var timeout=CancellationTokenSource.CreateLinkedTokenSource(stop.Token)) {
    timeout.CancelAfter(TimeSpan.FromSeconds(30));
    using(var response=await client.GetAsync(Engine.Channel,timeout.Token)) {
     response.EnsureSuccessStatusCode();release=Engine.Verify(await response.Content.ReadAsStringAsync());
    }
   }
   Installed current=null;
   try {current=Engine.Current();} catch(IOException) { }
   if(current!=null && Version.Parse(current.version)>=Version.Parse(release.version)) {await Launch(current);return;}
   status.Text=current==null?"Установить актуальную версию лаунчера":"Выпущено обновление лаунчера "+release.version;
   detail.Text=(release.notes??"")+"\nРазмер: "+(release.size/1048576.0).ToString("F1")+" МБ";
   update.Visible=true;
  } catch(OperationCanceledException) {if(!stop.IsCancellationRequested) Failure("Проверка обновлений заняла слишком много времени.");}
    catch(Exception ex) {Failure(ex.Message);}
 }
 void Failure(string message) {
  if(IsDisposed) return;
  status.Text="Не удалось завершить обновление";detail.Text=message;
  update.Visible=false;cancel.Text="Закрыть";
 }
 async Task Install() {
  if(busy)return;busy=true;update.Enabled=false;
  string download=Path.Combine(Engine.Root,"download-"+Guid.NewGuid().ToString("N")+".zip");
  try {
   status.Text="Скачиваем лаунчер "+release.version;
   using(var client=Engine.Client()) using(var response=await client.GetAsync(release.url,HttpCompletionOption.ResponseHeadersRead,stop.Token)) {
    response.EnsureSuccessStatusCode();
    using(var input=await response.Content.ReadAsStreamAsync()) using(var output=new FileStream(download,FileMode.CreateNew,FileAccess.Write,FileShare.None,131072,true)) {
     byte[] buffer=new byte[131072];long total=0;int count;
     while((count=await input.ReadAsync(buffer,0,buffer.Length,stop.Token))>0) {
      total+=count;if(total>release.size) throw new IOException("Сервер вернул пакет неожиданного размера.");
      await output.WriteAsync(buffer,0,count,stop.Token);
      progress.Value=(int)Math.Min(100,total*100/release.size);
      detail.Text=String.Format("{0:F1} / {1:F1} МБ — {2}%",total/1048576.0,release.size/1048576.0,progress.Value);
     }
    }
   }
   stop.Token.ThrowIfCancellationRequested();
   status.Text="Проверяем и устанавливаем обновление…";detail.Text="Проверка SHA-256 и распаковка файлов";
   // Do not interrupt a commit: the old pointer remains valid until the new package is complete.
   cancel.Enabled=false;
   Installed installed=await Task.Run(()=>Engine.Commit(download,release));
   if(!stop.IsCancellationRequested) await Launch(installed);
  } catch(OperationCanceledException) { }
    catch(Exception ex) {Failure(ex.Message);cancel.Enabled=true;}
  finally {busy=false;if(File.Exists(download))File.Delete(download);}
 }
 async Task Launch(Installed installed) {
  status.Text="Запускаем Project Battle…";detail.Text="Версия лаунчера "+installed.version;progress.Value=100;
  string dir=Engine.SafePath(Path.Combine(Engine.Root,"versions"),installed.directory);
  var start=new ProcessStartInfo(Engine.SafePath(dir,installed.entry));
  start.WorkingDirectory=Path.GetDirectoryName(start.FileName); start.UseShellExecute=false;
  start.EnvironmentVariables["PATH"]=Path.Combine(dir,"java17","bin")+";"+Environment.GetEnvironmentVariable("PATH");
  // Independent launcher settings and game directory; same remote accounts and game manifest.
  string profile=Path.Combine(Engine.Root,"profile");Directory.CreateDirectory(profile);
  start.EnvironmentVariables["APPDATA"]=profile;
  string temp=Path.Combine(profile,"temp");Directory.CreateDirectory(temp);
  start.EnvironmentVariables["TEMP"]=temp;start.EnvironmentVariables["TMP"]=temp;
  start.EnvironmentVariables["PB_BOOTSTRAP_EXE"]=Application.ExecutablePath;
  start.EnvironmentVariables["PB_BOOTSTRAP_PID"]=Process.GetCurrentProcess().Id.ToString();
  var process=Process.Start(start);
  Hide();await Task.Run(()=>process.WaitForExit());process.Dispose();Close();
 }
}
public static class Program {
 [STAThread] public static void Main(string[] args) {
  Engine.PublicKey=Encoding.UTF8.GetString(ReadResource("public-key.xml")).TrimStart('\uFEFF');
  bool first;using(var mutex=new Mutex(true,"Local\\ProjectBattleAuto.Bootstrap",out first)) {
   if(!first){MessageBox.Show("Project Battle Auto уже запущен.","Project Battle");return;}
   Application.EnableVisualStyles();Application.SetCompatibleTextRenderingDefault(false);
   try {Application.Run(new Window());} catch(Exception ex) {MessageBox.Show(ex.Message,"Project Battle");}
  }
 }
 static byte[] ReadResource(string name) {using(var s=typeof(Program).Assembly.GetManifestResourceStream(name)) using(var m=new MemoryStream()){s.CopyTo(m);return m.ToArray();}}
}
}
