Для алгоритма распознования лиц необходим OpenCV-android-sdk. Для этого:
  1) Скачиваем версию OpenCV 4.x с оф. сайта - https://opencv.org/releases/
  2) Для подключения библиотеки необходимо изменить путь в [gradle.propeties](https://github.com/4rsess/firstHitsApp/blob/master/gradle.properties). `opencvsdk = путь` 
  3) (необязательно) После синхронизации gradle файлов, может возникнуть ошибка связанная с Java/JPI, тогда надо в файле gradle.properties (:opencv) поменять версию на 17
        ```
        android{
             ...
             compileOptions {
                 sourceCompatibility JavaVersion.VERSION_17
                 targetCompatibility JavaVersion.VERSION_17
             }
             ...
         }
        ```
        
