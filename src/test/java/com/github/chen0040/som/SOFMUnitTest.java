package com.github.chen0040.som;


import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataQuery;
import com.github.chen0040.data.frame.DataRow;
import com.github.chen0040.data.frame.Sampler;
import com.github.chen0040.data.image.ImageDataFrameFactory;
import com.github.chen0040.som.utils.FileUtils;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.testng.Assert.*;


/**
 * Created by xschen on 25/5/2017.
 */
public class SOFMUnitTest {

   private Random rand = new Random();

   public double rand(){
      return rand.nextDouble();
   }

   public double rand(double lower, double upper){
      return rand() * (upper - lower) + lower;
   }

   public double randn(){
      double u1 = rand();
      double u2 = rand();
      double r = Math.sqrt(-2.0 * Math.log(u1));
      double theta = 2.0 * Math.PI * u2;
      return r * Math.sin(theta);
   }

   @Test
   public void testSimple(){


      DataQuery.DataFrameQueryBuilder schema = DataQuery.blank()
              .newInput("c1")
              .newInput("c2")
              .newOutput("designed")
              .end();

      Sampler.DataSampleBuilder negativeSampler = new Sampler()
              .forColumn("c1").generate((name, index) -> randn() * 0.3 + (index % 2 == 0 ? 2 : 4))
              .forColumn("c2").generate((name, index) -> randn() * 0.3 + (index % 2 == 0 ? 2 : 4))
              .forColumn("designed").generate((name, index) -> 0.0)
              .end();

      Sampler.DataSampleBuilder positiveSampler = new Sampler()
              .forColumn("c1").generate((name, index) -> rand(-4, -2))
              .forColumn("c2").generate((name, index) -> rand(-2, -4))
              .forColumn("designed").generate((name, index) -> 1.0)
              .end();

      DataFrame data = schema.build();

      data = negativeSampler.sample(data, 50);
      data = positiveSampler.sample(data, 50);

      System.out.println(data.head(10));

      SOFM algorithm = new SOFM();
      algorithm.setColumnCount(2);
      algorithm.setRowCount(1);

      DataFrame learnedData = algorithm.fitAndTransform(data);

      for(int i = 0; i < learnedData.rowCount(); ++i){
         DataRow tuple = learnedData.row(i);
         String clusterId = tuple.getCategoricalTargetCell("cluster");
         System.out.println("learned: " + clusterId +"\tknown: "+tuple.target());
      }


   }

   @Test
   public void test_image_segmentation() throws IOException {
      BufferedImage img= ImageIO.read(FileUtils.getResource("1.jpg"));

      DataFrame batch = ImageDataFrameFactory.dataFrame(img);

      SOFM cluster = new SOFM();
      cluster.fit(batch);

      List<Integer> classColors = new ArrayList<Integer>();
      for(int i=0; i < 5; ++i){
         for(int j=0; j < 5; ++j){
            classColors.add(ImageDataFrameFactory.get_rgb(255, rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
         }
      }

      BufferedImage segmented_image = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
      for(int x=0; x < img.getWidth(); x++)
      {
         for(int y=0; y < img.getHeight(); y++)
         {
            int rgb = img.getRGB(x, y);

            DataRow tuple = ImageDataFrameFactory.getPixelTuple(batch, rgb);

            int clusterIndex = cluster.transform(tuple);

            rgb = classColors.get(clusterIndex % classColors.size());

            segmented_image.setRGB(x, y, rgb);
         }
      }

      SOFMNet net = cluster.getNet();
      cluster.getDataNormalization();
      cluster.getEta0();

      for(SOFMNeuron n : net.getNeurons()){
         System.out.println("(" + n.getX() + ", " + n.getY() + "): " + n.getOutput());
      }

   }
}
