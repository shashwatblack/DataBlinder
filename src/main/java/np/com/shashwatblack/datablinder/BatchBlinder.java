package np.com.shashwatblack.datablinder;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;


/**
 * Created by shashwat on 12/31/14.
 */
public class BatchBlinder {

    String inputFile;
    String outputFile;
    String blindingLogic;

    public BatchBlinder(String inputFile, String blindingLogic, String outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.blindingLogic = blindingLogic;
    }

    public boolean run() {
        JSONParser jsonParser = new JSONParser();
        JSONObject blindingCues = null;
        DataBlinder blinder = new DataBlinder();
        try {
            blindingCues = (JSONObject) jsonParser.parse(new FileReader(blindingLogic));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            CSVReader reader = new CSVReader(new FileReader(inputFile));
            CSVWriter writer = new CSVWriter(new FileWriter(outputFile));
            String [] header = reader.readNext();
            writer.writeNext(header);
            String [] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                String [] nextOut = new String[nextLine.length];
                // nextLine[] is an array of values from the line
                for (int i=0; i<nextLine.length; i++) {
                    nextOut[i] = blinder.blind(nextLine[i], blindingCues.get(header[i]).toString());
                }
                //System.out.println(nextOut[0] + '-' + nextOut[1]);
                writer.writeNext(nextOut);
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }



    public static void main(String[] args) {

        BatchBlinder batchBlinder = new BatchBlinder("../DataBlinder/dummydata/sample.csv",
                "../DataBlinder/dummydata/BlindingLogic.json",
                "../DataBlinder/dummydata/output.csv");
        batchBlinder.run();


    }



}