# java-som
Package provides java implementation of self-organizing feature map (Kohonen map)

[![Build Status](https://travis-ci.org/chen0040/java-som.svg?branch=master)](https://travis-ci.org/chen0040/java-som) [![Coverage Status](https://coveralls.io/repos/github/chen0040/java-som/badge.svg?branch=master)](https://coveralls.io/github/chen0040/java-som?branch=master) 

# Install

Add the following dependency to your POM file:

```xml
<dependency>
  <groupId>com.github.chen0040</groupId>
  <artifactId>java-som</artifactId>
  <version>1.0.1</version>
</dependency>
```

# Usage

### Image Segmentation (Clustering) using SOFM

The following sample code shows how to use SOFM to perform image segmentation:

```java
BufferedImage img= ImageIO.read(FileUtils.getResource("1.jpg"));

DataFrame dataFrame = ImageDataFrameFactory.dataFrame(img);

SOFM cluster = new SOFM();
cluster.fit(dataFrame);

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

    DataRow tuple = ImageDataFrameFactory.getPixelTuple(dataFrame, rgb);

    int clusterIndex = cluster.transform(tuple);

    rgb = classColors.get(clusterIndex % classColors.size());

    segmented_image.setRGB(x, y, rgb);
 }
}
```




