# CommentCounter
This program detects single-line, multi-line and javadoc comment lines of functions in a Java file given from the console and aims to save each comment type in separate files according to the function names. 

Regular Expressions are used in this program.

Also there is a jar file in dist folder. You can use it.

![Ekran Alıntısı](https://user-images.githubusercontent.com/72921635/233138637-c1d6952f-db09-4955-aa4e-56ff03e77f26.PNG)

--------------------------------------------------------------------------------------
Bu program Sakarya Üniversitesi Bilgisayar Mühendisliği bölümü Programlama Dilleri ve Pradigmaları dersi için hazırlanmıştır. 
Ödevin içeriği:
Yazacağınız Eclipse projesi Java dilinde ve konsol uygulaması olmalıdır.
Programa konsol üzerinden komut satırı parametresi olarak verilecek .java dosyasını, program okuyacak ve ilk önce tüm fonksiyonları tespit edecektir. Daha sonra fonksiyonların içinde yazılmış olan yorumları ve Javadoc’ları ayrı ayrı fonksiyonlar ile ilişkili olarak tutacaktır. Ayrıca fonksiyonun üstünde yazılı fonksiyona ait olan javadoc varsa onları da alacaktır. Ekranda her fonksiyona ait kaç adet tek satır yorum (// yorumlar), çok satırlı yorum (/ * yorumlar * /) ve javadoc (/** yorumlar * /) olduğunu listeleyecektir. Yorumlar 3 farklı dosyaya kaydedilmelidir. Javadoc yorumlari javadoc.txt, tek satır yorumları teksatir.txt ve çok satırlı yorumlar coksatir.txt ye aşağıdaki formatta kaydedilmelidir. 3 dosyanın da formatı aynı olmalıdır.
