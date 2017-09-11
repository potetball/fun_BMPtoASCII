package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Form
{
    private static Form canvasSingleton;
    /**
     * Factory method to get the canvas singleton object.
     */
    public static Form getCanvas()
    {
        if(canvasSingleton == null) {
            canvasSingleton = new Form("Simple Filestream Reader With Form", 500, 300,
                    Color.white);
        }
        canvasSingleton.setVisible(true);
        return canvasSingleton;
    }

    //  ----- instance part -----

    private JFrame frame;
    private CanvasPane canvas;
    private Graphics2D graphic;
    private Color backgroundColor;
    private Image canvasImage;
    private int printLine = 1;
    public static final int BITS_PER_BYTE = 8;


    private Form(String title, int width, int height, Color bgColor)
    {
        frame = new JFrame();
        canvas = new CanvasPane();
        frame.setContentPane(canvas);
        frame.setTitle(title);
        frame.setLocation(30, 30);
        canvas.setPreferredSize(new Dimension(width, height));
        backgroundColor = bgColor;
        frame.pack();
    }

    public void setVisible(boolean visible)
    {
        if(graphic == null) {
            // first time: instantiate the offscreen image and fill it with
            // the background color
            Dimension size = canvas.getSize();
            canvasImage = canvas.createImage(size.width, size.height);
            graphic = (Graphics2D)canvasImage.getGraphics();
            graphic.setColor(backgroundColor);
            graphic.fillRect(0, 0, size.width, size.height);
            graphic.setColor(Color.black);
        }
        frame.setVisible(visible);
    }


    /**
     * Wait for a specified number of milliseconds before finishing.
     * This provides an easy way to specify a small delay which can be
     * used when producing animations.
     * @param  milliseconds  the number
     */
    public void wait(int milliseconds)
    {
        try
        {
            Thread.sleep(milliseconds);
        }
        catch (Exception e)
        {
            // ignoring exception at the moment
        }
    }



    /**
     * Erase the whole canvas. (Does not repaint.)
     */
    private void erase()
    {
        Color original = graphic.getColor();
        graphic.setColor(backgroundColor);
        Dimension size = canvas.getSize();
        graphic.fill(new Rectangle(0, 0, size.width, size.height));
        graphic.setColor(original);
    }

    /************************************************************************
     * Inner class CanvasPane - the actual canvas component contained in the
     * Canvas frame. This is essentially a JPanel with added capability to
     * refresh the image drawn on it.
     */
    private class CanvasPane extends JPanel
    {
        private static final long serialVersionUID = 1L;
        public void paint(Graphics g)
        {
            g.drawImage(canvasImage, 0, 0, null);
        }
    }
    public static long getUnsignedInt(int x) {
        ByteBuffer buf = ByteBuffer.allocate(Long.SIZE / BITS_PER_BYTE);
        buf.putInt(Integer.SIZE / BITS_PER_BYTE, x);
        return buf.getLong(0);
    }

    public void printScreen(String outString) {
        System.out.println("Printing to screen: " + outString);
        Graphics2D g = graphic;
        g.setColor(Color.black);
        g.drawString(outString,0,printLine * 15);
        printLine = printLine+ 1;
        canvas.repaint();
    }
    public void debugScreen(String outString) {
        System.out.println(outString);
    }
    //Form code
    public void ReadFile(String bitmapFile) {
        printScreen("Loading file: " + bitmapFile);
        try {
            //Define our image
            Image image;

            RandomAccessFile File = new RandomAccessFile(bitmapFile,"r");
            //BMP File Attributes
            byte[] bFileType = new byte[2];
            int bSize;
            short bReserved1; //Must be zero
            short bReserved2; //Must be zero
            int bOffBits; //OFfset to start bitmaps

            //Read header
            int biLen = 14; //Bitmap header size
            byte[] biHead = new byte[biLen];
            File.read(biHead,0,14);
            //Read first 2 char

            String strFileType = new String(new char[]{(char)biHead[0],(char)biHead[1]});
            //Check if its a bitmap
            if (!strFileType.equals("BM")) {
                throw new Exception("Not a BMP");
            }

            bSize = (((int)biHead[5]&0xff)<<24)
                    | (((int)biHead[4]&0xff)<<16)
                    | (((int)biHead[3]&0xff)<<8)
                    | (int)biHead[2]&0xff;

            debugScreen("Filesize: " + (bSize)+ " Bytes");

            //BMP Image Header attributes
            int biInfoHeaderLen = 40; //LENGTH OF BITMAPINFOHEADER
            byte[] biInfoHeader = new byte[biInfoHeaderLen];
            File.read(biInfoHeader,0,biInfoHeaderLen);
            int biSize = (((int)biInfoHeader[3]&0xff)<<24)
                    | (((int)biInfoHeader[2]&0xff)<<16)
                    | (((int)biInfoHeader[3]&0xff)<<8)
                    | (int)biInfoHeader[0]&0xff;

            System.out.println("Bitmap info size: " + biSize);

            int nwidth = (((int)biInfoHeader[7]&0xff)<<24)
                    | (((int)biInfoHeader[6]&0xff)<<16)
                    | (((int)biInfoHeader[5]&0xff)<<8)
                    | (int)biInfoHeader[4]&0xff;
            
            
            System.out.println("Width is :"+nwidth);
            int nheight = (((int)biInfoHeader[11]&0xff)<<24)
                    | (((int)biInfoHeader[10]&0xff)<<16)
                    | (((int)biInfoHeader[9]&0xff)<<8)
                    | (int)biInfoHeader[8]&0xff;
            System.out.println("Height is :"+nheight);
            int nplanes = (((int)biInfoHeader[13]&0xff)<<8) | (int)biInfoHeader[12]&0xff;
            System.out.println("Planes is :"+nplanes);
            int nbitcount = (((int)biInfoHeader[15]&0xff)<<8) | (int)biInfoHeader[14]&0xff;
            System.out.println("BitCount is :"+nbitcount);
            // Look for non-zero values to indicate compression
            int ncompression = (((int)biInfoHeader[19])<<24)
                    | (((int)biInfoHeader[18])<<16)
                    | (((int)biInfoHeader[17])<<8)
                    | (int)biInfoHeader[16];
            System.out.println("Compression is :"+ncompression);
            int nsizeimage = (((int)biInfoHeader[23]&0xff)<<24)
                    | (((int)biInfoHeader[22]&0xff)<<16)
                    | (((int)biInfoHeader[21]&0xff)<<8)
                    | (int)biInfoHeader[20]&0xff;
            System.out.println("SizeImage is :"+nsizeimage);
            int nxpm = (((int)biInfoHeader[27]&0xff)<<24)
                    | (((int)biInfoHeader[26]&0xff)<<16)
                    | (((int)biInfoHeader[25]&0xff)<<8)
                    | (int)biInfoHeader[24]&0xff;
            System.out.println("X-Pixels per meter is :"+nxpm);
            int nypm = (((int)biInfoHeader[31]&0xff)<<24)
                    | (((int)biInfoHeader[30]&0xff)<<16)
                    | (((int)biInfoHeader[29]&0xff)<<8)
                    | (int)biInfoHeader[28]&0xff;
            System.out.println("Y-Pixels per meter is :"+nypm);
            int nclrused = (((int)biInfoHeader[35]&0xff)<<24)
                    | (((int)biInfoHeader[34]&0xff)<<16)
                    | (((int)biInfoHeader[33]&0xff)<<8)
                    | (int)biInfoHeader[32]&0xff;
            System.out.println("Colors used are :"+nclrused);
            int nclrimp = (((int)biInfoHeader[39]&0xff)<<24)
                    | (((int)biInfoHeader[38]&0xff)<<16)
                    | (((int)biInfoHeader[37]&0xff)<<8)
                    | (int)biInfoHeader[36]&0xff;
            System.out.println("Colors important are :"+nclrimp);
            if (nbitcount==24) {
                // No Palatte data for 24-bit format but scan lines are
                // padded out to even 4-byte boundaries.
                int npad = (nsizeimage / nheight) - nwidth * 3;
                int ndata[] = new int[nheight * nwidth];
                byte brgb[] = new byte[(nwidth + npad) * 3 * nheight];

                File.read (brgb, 0, (nwidth + npad) * 3 * nheight);
                int nindex = 0;
                for (int j = 0; j < nheight; j++)
                {
                    for (int i = 0; i < nwidth; i++)
                    {
                        ndata [nwidth * (nheight - j - 1) + i] =
                                (255&0xff)<<24
                                        | (((int)brgb[nindex+2]&0xff)<<16)
                                        | (((int)brgb[nindex+1]&0xff)<<8)
                                        | (int)brgb[nindex]&0xff;
                        System.out.println("Encoded Color at ("
                        +i+","+j+")is: (R,G,B)= ("
                                +((int)(brgb[2]) & 0xff)+","
                                +((int)brgb[1]&0xff)+","
                                +((int)brgb[0]&0xff)+")");

                        nindex += 3;
                    }
                    nindex += npad;
                }

                image = Toolkit.getDefaultToolkit().createImage
                        (new MemoryImageSource(nwidth, nheight,
                                ndata, 0, nwidth));
            }
            else if (nbitcount == 8)
            {
                // Have to determine the number of colors, the clrsused
                // parameter is dominant if it is greater than zero.  If
                // zero, calculate colors based on bitsperpixel.
                int nNumColors = 0;
                if (nclrused > 0)
                {
                    nNumColors = nclrused;
                }
                else
                {
                    nNumColors = (1&0xff)<<nbitcount;
                }
                System.out.println("The number of Colors is"+nNumColors);
                // Some bitmaps do not have the sizeimage field calculated
                // Ferret out these cases and fix 'em.
                if (nsizeimage == 0)
                {
                    nsizeimage = ((((nwidth*nbitcount)+31) & ~31 ) >> 3);
                    nsizeimage *= nheight;
                    System.out.println("nsizeimage (backup) is"+nsizeimage);
                }
                // Read the palatte colors.
                int  npalette[] = new int [nNumColors];
                byte bpalette[] = new byte [nNumColors*4];
                File.read (bpalette, 0, nNumColors*4);
                int nindex8 = 0;
                for (int n = 0; n < nNumColors; n++)
                {
                    npalette[n] = (255&0xff)<<24
                            | (((int)bpalette[nindex8+2]&0xff)<<16)
                            | (((int)bpalette[nindex8+1]&0xff)<<8)
                            | (int)bpalette[nindex8]&0xff;
                    System.out.println ("Palette Color "+n
                    +" is:"+npalette[n]+" (res,R,G,B)= ("
                            +((int)(bpalette[nindex8+3]) & 0xff)+","
                            +((int)(bpalette[nindex8+2]) & 0xff)+","
                            +((int)bpalette[nindex8+1]&0xff)+","
                            +((int)bpalette[nindex8]&0xff)+")");
                    nindex8 += 4;
                }
                // Read the image data (actually indices into the palette)
                // Scan lines are still padded out to even 4-byte
                // boundaries.
                int npad8 = (nsizeimage / nheight) - nwidth;
                System.out.println("nPad is:"+npad8);
                int  ndata8[] = new int [nwidth*nheight];
                byte bdata[] = new byte [(nwidth+npad8)*nheight];

                //Read the actual image, and store it
                //  in an uncompressed array
                File.read (bdata, 0, (nwidth+npad8)*nheight);
                nindex8 = 0;
                for (int j8 = 0; j8 < nheight; j8++)
                {
                    Color[] nColor = new Color[nwidth];
                    int[] nIntensity = new int[nwidth];
                    String strLineOut = new String();
                    for (int i8 = 0; i8 < nwidth; i8++)
                    {
                        ndata8 [nwidth*(nheight-j8-1)+i8] =
                                npalette [((int)bdata[nindex8]&0xff)];
                        int argb = ndata8[nwidth*(nheight-j8-1)+i8];
                        int r = (argb)&0xFF;
                        int g = (argb>>8)&0xFF;
                        int b = (argb>>16)&0xFF;
                        int a = (argb>>24)&0xFF;

                        nColor[i8] = new Color(r,g,b,a) ;
                        nIntensity[i8] = (r >>2)+ (g>>1) + (b>>2);
                        strLineOut += returnStrNeg(nIntensity[i8]);
                        nindex8++;
                    }
                    System.out.println(strLineOut);
                    nindex8 += npad8;
                }
                image = Toolkit.getDefaultToolkit().createImage
                        ( new MemoryImageSource (nwidth, nheight,
                                ndata8, 0, nwidth));

            }
            else
            {
                System.out.println ("Not a 24-bit or 8-bit Windows Bitmap, aborting...");
                image = (Image)null;
            }
            File.close();
            graphic.drawImage(image,0,0,null);

        }
        catch (FileNotFoundException f) {
            printScreen("File not found: " + bitmapFile);
        }
        catch (IOException err) {
            printScreen("IO Error: " + err.getMessage());
        }
        catch (Exception exp) {
            printScreen("Not a .BMP file");
        }


        printScreen("Done...");
    }
    private char returnStrNeg(double g) {
        final char str;

        if (g >= 230.0) {
            str = '@';
        } else if (g >= 200.0) {
            str = '#';
        } else if (g >= 180.0) {
            str = '8';
        } else if (g >= 160.0) {
            str = '&';
        } else if (g >= 130.0) {
            str = 'o';
        } else if (g >= 100.0) {
            str = ':';
        } else if (g >= 70.0) {
            str = '*';
        } else if (g >= 50.0) {
            str = '.';
        } else {
            str = ' ';
        }
        return str;

    }
}

