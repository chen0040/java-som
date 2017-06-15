package com.github.chen0040.som;

import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataRow;
import com.github.chen0040.data.utils.transforms.Standardization;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Vector;

/**
 * Created by xschen on 20/8/15.
 */
@Getter
@Setter
public class SOFM {
    @Setter(AccessLevel.NONE)
    private SOFMNet net;
    @Setter(AccessLevel.NONE)
    private Standardization dataNormalization;

    private int rowCount = 5;
    private int columnCount = 5;
    private double eta0 = 0.1;


    public SOFM(){
    }

    public int transform(DataRow tuple) {

        double[] x = tuple.toArray();
        x = dataNormalization.standardize(x);

        SOFMNeuron winner=net.match(x);
        return winner.getOutput();
    }

    public DataFrame fitAndTransform(DataFrame dataFrame) {
        dataFrame = dataFrame.makeCopy();
        fit(dataFrame);
        for(int i = 0; i < dataFrame.rowCount(); ++i) {
            DataRow row = dataFrame.row(i);
            int clusterId = transform(row);
            row.setCategoricalTargetCell("cluster", "" + clusterId);
        }
        return dataFrame;
    }

    public void fit(DataFrame batch) {

        int dimension = batch.row(0).toArray().length;
        int m = batch.rowCount();

        dataNormalization = new Standardization(batch);

        //number of neuron rows is [Rows], number of neuron cols is [Cols], input dimension is [Input Dimension]
        net = new SOFMNet(rowCount, columnCount, dimension);
        net.setEta0(eta0);
        net.setSelfOrganizingPhaseEpoches(1000); //SOM training consists of self-organizing phase and converging phase, this parameter specifies the number of training inputs for self-organizing phase, note that an epoch simply means a training input here

        //initialize weights on the SOM network
        Vector<Double> weight_lower_bounds=new Vector<Double>();
        Vector<Double> weight_upper_bounds=new Vector<Double>();
        for(int i=0; i < dimension; i++)
        {
            weight_lower_bounds.add(-1.0); //lower bound for each input dimension is [Weight Lower Bound]
            weight_upper_bounds.add(1.0); //upper bound for each input dimension is [Weight Upper Bound]
        }
        net.initialize(weight_lower_bounds, weight_upper_bounds);

        //set unique label for each neuron in SOM net
        for(int r=0; r < rowCount; ++r)
        {
            for(int c=0; c < columnCount; ++c)
            {
                net.neuronAt(r, c).setOutput(r * columnCount + c); //neuron at row r and column c will have label "[r, c]"
            }
        }

        //the SOM net can be trained using a typical 3000 training inputs, repeated the above code for other training inputs
        for(int i=0; i < m; ++i){
            DataRow tuple = batch.row(i);
            double[] x = tuple.toArray();
            x = dataNormalization.standardize(x);
            net.train(x);
        }
    }

}
