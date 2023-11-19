package com.example;

import processing.core.PApplet;

public class ClassicalRhythms {

    PApplet myMain;

    ClassicalRhythms(PApplet mainProgram)
    {
        myMain = mainProgram;
    }

    void printOut()
    {
        myMain.println("hello");
    }

    void draw()
    {
        myMain.rect(40, 40, 40, 40);

    }

}
