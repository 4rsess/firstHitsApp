Для алгоритма распознования лиц необходим OpenCV-android-sdk. Для этого:
  1) Скачиваем версию 4.x с оф. сайта - https://opencv.org/releases/
  2) Для подключения библиотеки необходимо указать путь до папки с ней в файле gradle.propeties (изначально путь указан на папку в корне диска С)
  3) (необязательно) После синхронизации gradle файлов, может возникнуть ошибка связанная с Java/JPI, тогда надо в файле gradle.properties (:opencv) поменять версию на 17
  4)   private static final String TF_OD_API_MODEL_FILE = "D:\\Documents\\EclipseProjects\\Java-Wrapper-Object-Detector-TF\\src\\main\\resources\\ssd_mobilenet_v1_android_export.pb";
  
  private static final String TF_OD_API_LABELS_FILE = "D:\\Documents\\EclipseProjects\\Java-Wrapper-Object-Detector-TF\\src\\main\\resources\\coco_labels_list.txt";
    
  inputImage = ImageIO.read(new File("D:\\Documents\\EclipseProjects\\Java-Wrapper-Object-Detector-TF\\src\\main\\resources\\6.jpg"));
         android{
             ...
             compileOptions {
                 sourceCompatibility JavaVersion.VERSION_17
                 targetCompatibility JavaVersion.VERSION_17
             }
             ...
         }
