# SocialMediaApplication
Türkçe: <br>
Burada, Android becerilerimi ilerletmek amacıyla geliştirdiğim bir Sosyal Medya Uygulamasını tanıtıyor olacağım. Tanıtımı hem Türkçe hem İngilizce olacak şekilde iki dilde yazıyor olacağım. Uygulamanın dili Türkçe'dir. İleride kullanıcının cihaz diline göre İngilizce veya diğer dilleri bir seçenek olarak ekleyebilirim.

English: <br>
Here, I will be introducing a Social Media App that I developed to improve my Android skills. I will be writing the introduction in two languages, both Turkish and English. The language of the app is Turkish. In the future, I may add English or other languages as an option depending on the user's device language.

Uygulama Hakkında (About the App)
-
Türkçe: <br>
Bir Sosyal Medya Uygulamasında olması gereken tüm temel özellikleri içeren bir uygulamadır. 
<br> Uygulamaya Kayıt Olmak, Uygulamaya Giriş Yapmak, Diğer Kullanıcıları Bulmak, Kullanıcılarla Mesajlaşmak, Şifre-Kullanıcı Adı Değiştirmek gibi Hesap İşlemleri vesaire... 

Uygulamayı çalıştırmak için "build" etmek istiyorsanız, lütfen kendi google-services.json dosyanızı root klasörüne koyun. (Bu dosyayı Google Firebase Realtime Veritabanınızı oluşturduğunuzda alacaksınız). Çünkü uygulama veritabanı Google Firebase Database ile çalışmaktadır.

English: <br>
It is an application that includes all the basic features that should be in a Social Media Application. 
<br> Account Operations such as Registering to the Application, Logging into the Application, Finding Other Users, Messaging with Users, Changing Password-User Name, etc... 

If you want to build the application to run it, please put your own google-services.json file in the root folder. (You will get this file when you create your Google Firebase Realtime Database).Because the application database works with Google Firebase Database.

1.Uygulamaya kayıt olmak ( Sign up for the app)
-
Türkçe: <br>
Kayıt ol butonuna tıklanarak uygulamaya kayıt olmak için bilgilerimizi gireceğimiz sayfa açılır. Ardından, gereken bilgiler eksiksiz doldurulur. Eğer eksiksiz doldurulmazsa ilerleme butonu aktif olmaz. Dolayısıyla eksik bilgi ile kayıt olmak mümkün değildir.Bilgilerin eksik ya da istenilen formatta girilmediği durumlarda kullanıcıya bildiren uyarılar da eklenmiştir. Örneğin, "şifreniz en az 6 karakter uzunluğunda olmalı", "lütfen hiç boş yer bırakmadan doldurunuz." gibi basit uyarılar ve kontrol yapıları eklenmiştir.

Profile yazılan açıklama da yine kayıt olma sayfasına eklenmiştir. Bu tamamen test amaçlıdır. İleride, başka bir sayfaya eklenebilir ve kayıt olma sayfasından çıkarabilir.

English: <br>
Click on the "Kayıt Ol" (Sign Up) button to open the page where we fill our information to sign up for the application. Then, the required information is filled in completely. If the information is not filled in completely, the progress button will not be active. Therefore, it is not possible to register with missing information. Warnings have also been added to notify the user if the information is missing or not entered in the desired format. For example, simple warnings and control structures have been added, such as "your password must be at least 6 characters long", "fill without leaving blank spaces".

The description written in the profile has also been added to the registration page. This is purely for testing purposes. In the future, it can be added to another page and removed from the registration page. 

![Media_230712_163330](https://github.com/projectOrhan/SocialMediaApplication/assets/28529085/140e2242-ab08-4fed-af5e-b6f6ee76ac5a)

2.Hesaba Giriş Yapmak ( Login Account)
-
Örnek olarak halihazırda eklediğim test hesaplarından birine giriş yapmak istersem aşağıdaki gibi yapabiliyorum.
For example, if I want to log in to one of the test accounts I have already added, I can do as shown below.

![Media_230710_121832](https://github.com/projectOrhan/SocialMediaApplication/assets/28529085/949f6177-81e0-4d64-8c39-415966390660)

3.Diğer Kullanıcıları Bulmak (Discovering Other Users)
-
Türkçe: <br>
Uygulamaya kayıt olmuş diğer kullanıcıları listeleyen bir sayfa da ugulamaya eklenmiştir. Bu sayfa "Keşfet" olarak adlandırılmıştır, aşağıdaki butonlardan ulaşılabilir.

Bu sayfada, kullanıcı iletişime geçebileceği uygulamayı kullanan tüm kullanıcıları (kendisi hariç) bir liste halinde görebilme imkanına sahip olur. Böylece dilediği kullanıcı ile etkileşime geçebilir, iletişime geçmek istediğinde ise, 

Örneğin bir kişinin profilini daha detaylı görmek istediğinde profil resminin üzerine tıklaması yeterlidir.
Mesela, profilini beğenmiş olabilir, dolayısıyla mesaj atmak isteyebilir, bu durumda da kullanıcının üzerine tıklaması yeterlidir. Yani fotoğraf kısmı hariç, kullanıcının listedeki konumuna tıklaması yeterlidir. Bu sayede, üzerine tıkladığı kişiye mesaj atabileceği sayfaya ulaşmış olur. Aşağıya eklediğim Gif dosyası ile nasıl çalıştığı gösterilmiştir.

İleride bu kısım daha az kullanıcıyı göstermek üzere düzenlenebilir. Şuan, test amaçlı olduğu için uygulamaya kayıtlı ne kadar hesap var ise o kadar hesabı bir liste halinde gösteriyor. Bu durum hesap sayısı çoğaldığında sıkıntı yaratacaktır. O yüzden, mesela maksimum 10 adet kullanıcıyı gösterecek şekilde düzenlenebilir. Sayfa her yenilediğinde uygulamayı kullanan diğer 10 kullanıcıyı rastgele olacak şekilde gösterilebilir. Bu gibi optimizasyon ve düzeltmeler ileride eklenebilir. 

English: <br>
A page listing other users who have registered with the app has also been added to the app. This page is called "Keşfet" (Discover) and can be accessed from the buttons below.

On this page, the user has the opportunity to see a list of all users (except himself) using the application that he can communicate with. Thus, he/she can interact with the user he/she wants to interact with, 

For example, if you want to see a person's profile in more detail, just click on their profile picture.
For example, he might like his profile, so he might want to send a message, in which case he just needs to click on the user. In other words, except for the photo, just click on the user's position in the list. This will take him to the page where he can send a message to the person he clicked on. The Gif file I have attached below shows how it works.

In the future, this section can be edited to show fewer users. Currently, since it is for testing purposes, it shows as many accounts as there are registered in the application in a list. This will be a problem when the number of accounts increases. Therefore, for example, it can be organized to show a maximum of 10 users. Each time the page is refreshed, it can randomly show the other 10 users using the application. Such optimizations and fixes can be added in the future. 

![Media_230712_231203](https://github.com/projectOrhan/SocialMediaApplication/assets/28529085/0a491390-276e-44ff-b935-d87f430c443c)

Yukarıda da gösterildiği üzere "Keşfet" sayfasında diğer kullanıcılar görüntülenmiştir ve etkileşime geçilmek istenen kullanıcı üzerine tıklanmıştır, istenildiği takdirde "Takip Et" butonu ile o kullanıcı takip edilmiştir, Mesaj Balonu simgesine tıklanarak o kişiye mesaj gönderebileceğimiz Sohbet Aktivitesi başlatılmıştır.

As shown above, other users are displayed on the "Keşfet" (Discover) page, click on the user you wish to interact with, follow that user with the "Takip Et" button if desired, click on the Message Bubble icon to start the Chat Activity where you can send a message to that person.






