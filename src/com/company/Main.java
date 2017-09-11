package com.company;

public class Main {

    public static void main(String[] args) {
        System.out.println("Whatsup");
        String bitimagePath = "C:\\Users\\Anonymous\\IdeaProjects\\SimpleFilestreamReaderWithForm\\printer.bmp";
        Form frmMain = Form.getCanvas();
        frmMain.ReadFile(bitimagePath);

    }
}
