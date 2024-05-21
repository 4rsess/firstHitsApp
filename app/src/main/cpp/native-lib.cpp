#include <jni.h>
#include <string>
#include <iostream>
#include <stdio.h>

#include "opencv2/objdetect/objdetect.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"

#include <opencv2/imgproc/types_c.h>


using namespace std;
using namespace cv;



CascadeClassifier face_cascade;

void detectFace(Mat& mat){
    vector<Rect> faces;
    Mat frame_gray;
    cvtColor( mat, frame_gray, COLOR_BGRA2GRAY );
    equalizeHist( frame_gray,frame_gray );

    //-- Detect faces
    face_cascade.detectMultiScale( frame_gray, faces, 1.1, 2, 0|CASCADE_SCALE_IMAGE, Size(30, 30) );
    for ( size_t i = 0; i < faces.size(); i++ )
    {
        Point center( faces[i].x + faces[i].width/2, faces[i].y + faces[i].height/2 );
        ellipse( mat, center, Size( faces[i].width/2, faces[i].height/2 ), 0, 0, 360, Scalar( 255, 0, 255 ), 4, 8, 0 );
        Mat faceROI = frame_gray( faces[i] );
    }

}

extern "C"{
    JNIEXPORT void JNICALL
    Java_com_example_photoeditor_FourthAlgorithm_findFaces(JNIEnv *env, jobject /* this */,jlong matAddr){

        Mat & mat = *(Mat *)matAddr;
        detectFace(mat);
    }
}









