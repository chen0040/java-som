# java-som
Package provides java implementation of self-organizing feature map (Kohonen map)

[![Build Status](https://travis-ci.org/chen0040/java-som.svg?branch=master)](https://travis-ci.org/chen0040/java-som) [![Coverage Status](https://coveralls.io/repos/github/chen0040/java-som/badge.svg?branch=master)](https://coveralls.io/github/chen0040/java-som?branch=master) 

# Install

Add the following dependency to your POM file:

```xml
<dependency>
  <groupId>com.github.chen0040</groupId>
  <artifactId>java-som</artifactId>
  <version>1.0.2</version>
</dependency>
```

# Usage

### Spatial clustering using SOFM

The sample code below shows how to cluster a set of 2-D points (c1, c2) in space using SOFM:

```java
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
// create a 1x2 SOM grid for 2-clusters
algorithm.setColumnCount(2);
algorithm.setRowCount(1);

DataFrame learnedData = algorithm.fitAndTransform(data);

for(int i = 0; i < learnedData.rowCount(); ++i){
 DataRow tuple = learnedData.row(i);
 String clusterId = tuple.getCategoricalTargetCell("cluster");
 System.out.println("learned: " + clusterId +"\tknown: "+tuple.target());
}
```

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




