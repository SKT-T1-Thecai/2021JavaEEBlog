package com.zl.utils;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

public class UploadFile {
    /**
     *
     * @param file
     * @param targetPath HeadImage/gugu.jpg
     * @return
     * @throws FileNotFoundException
     */
    public static boolean SaveFileToPath(MultipartFile file,String targetPath,boolean compress) throws FileNotFoundException {
            File path = new File(ResourceUtils.getURL("src/main/resources/").getPath());
            String baseUrl    = path.getAbsolutePath()+'/';
            try {
                if(!compress||file.getSize()<=100*1024)
                {
                    FileOutputStream fos = new FileOutputStream(baseUrl + targetPath);
                    fos.write(file.getBytes()); // 写入文件
                }
               else
                {
                    File newFile = new File(baseUrl+targetPath);
                    Thumbnails.of(file.getInputStream()).scale(1f).toFile(newFile);
                    //压缩图片至指定大小下
                    commpressPicCycle(baseUrl + targetPath,100*1024,0.8);
                    System.out.println(file.getSize());
                    //Thumbnails.of(image.getInputStream()).scale(0.7f).outputQuality(0.25f).toFile(file);
                    
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
    }
    public static String getFileType(MultipartFile file)
    {
        return Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf('.'));
    }

    // 压缩图片到指定大小
    private static void commpressPicCycle(String desPath, long desFileSize, double accuracy) throws IOException {
        File srcFileJPG = new File(desPath);
        //如果小于指定大小不压缩；如果大于等于指定大小压缩
        if (srcFileJPG.length() <= desFileSize) {
            return;
        }
        // 计算宽高
        BufferedImage bim = ImageIO.read(srcFileJPG);
        int desWidth = new BigDecimal(bim.getWidth()).multiply(new BigDecimal(accuracy)).intValue();
        int desHeight = new BigDecimal(bim.getHeight()).multiply(new BigDecimal(accuracy)).intValue();
        Thumbnails.of(desPath).size(desWidth, desHeight).outputQuality(accuracy).toFile(desPath);
        commpressPicCycle(desPath, desFileSize, accuracy);
    }
}
