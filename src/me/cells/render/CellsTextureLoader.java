package me.cells.render;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class CellsTextureLoader {

	//loadTexture method, taking the String loc parameter, and returning an integer as the textureID.
	//This method loads an image from a file location (loc), then calculates how many pixels are in the image.
	//Then gets each pixel individually and puts it inside a buffer to store the RGBA values of that pixel.
	//Then flips the buffer(as if it wasnt done the image would be in the opposite orentation.
	//Bind the buffer to a texture ID and store it in graphics memory to be used at a later time.
    public int loadTexture(String loc) {
        BufferedImage image = loadImage(loc);
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); //4 for RGBA, 3 for RGB

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                buffer.put((byte) (pixel & 0xFF));               // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
            }
        }

        buffer.flip();
        int textureID = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        return textureID;
    }

    //loadImage method taking String loc(file location) and returning a BufferedImage.
    //This uses the ImageIO class and reads the file from a file location. If it cant be found it prints the stack trace and returns null
    private BufferedImage loadImage(String loc) {
        try {
            return ImageIO.read(new File(loc));
        } catch (IOException e) {
        	System.out.println("Cant read file at " + loc);
            e.printStackTrace();
        }
        return null;
    }
}
