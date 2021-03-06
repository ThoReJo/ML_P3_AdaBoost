package spamclassificatie;
import java.util.*;
import java.lang.*;
import java.io.*;

public class SpamClassificatie {

    public static void main(String[] args) 
    {
        SpamClassificatie main = new SpamClassificatie();

        List<Mail> allData = new ArrayList<>();
        
        main.LoadFile("spambase.txt", allData);

		int numberOfDataSets = 5;
        
        List<List<Mail>> datasets = splitDataSet(allData, numberOfDataSets);
        
        for (int number_of_hypotheses = 10; number_of_hypotheses < 1000; number_of_hypotheses += 10)
        {
        	double error_total = 0;
        	for (int iterations = 0; iterations < 100; iterations++)
        	{
        		for (int i = 0; i < numberOfDataSets; i++)
        		{
        			List<Mail> currentDataSet = new ArrayList<Mail>();
        			List<Mail> validationSet = datasets.get(i);
        			for (int a = 0; a < numberOfDataSets; a++)
        				if (a!= i) currentDataSet.addAll(datasets.get(a));
        			AdaBoost adaBoost = new AdaBoost(number_of_hypotheses,currentDataSet);
        			double error = 0;
        			for (int a = 0; a < validationSet.size(); a++)
        			{
        				error += adaBoost.classify(validationSet.get(a)) == validationSet.get(a).y ? 0 : 1;
        			}
        			error /= validationSet.size();
        			error_total += error;
        		}
        	}
        System.out.println("m = " + number_of_hypotheses +
        		"\t| error: " + error_total/(numberOfDataSets*100));
        }
        //main.PrintList(allData);  
    }
    
    private static List<List<Mail>> splitDataSet(List<Mail> allData, int numberOfDataSets)
	{
    	int mailsPerSet = allData.size()/numberOfDataSets;
    	List<List<Mail>> datasets = new ArrayList<List<Mail>>();
    	for (int dataset_index = 0; dataset_index < numberOfDataSets; dataset_index++)
    	{
    		List<Mail> dataset = new ArrayList<Mail>();
    		// repeat mailsPerSet times:
    		for (int i = 0; i < mailsPerSet; i++)
    		{
    			// pick a random mail from allData,
    			int randomMail_index = (int) Math.random() * allData.size();
    			// add it to the current dataset and
    			dataset.add(allData.get(randomMail_index));
    			// remove it from allData
    			allData.remove(randomMail_index);
    		}
    		// add this dataset to the set of datasets
    		datasets.add(dataset);
    	}
    	// if there are still mails left in allData (the number of mails modulo numberOfDataSets != 0)
    	if (!allData.isEmpty())
    		// add them to the first dataset in the set
    		datasets.get(0).addAll(allData);
    	// return the set of datasets
		return datasets;
	}

	public void PrintList(List<Mail> dataList)
    {
        for (Mail mail : dataList)
        {
	    String mailInfo = mail.toString();
            System.out.println(mailInfo);
        }        
    }
    
    public void LoadFile(String filename, List<Mail> dataList)
    {
        File file = new File(filename);
        BufferedReader reader = null;
	
        try {
            reader = new BufferedReader(new FileReader(file));
            String text;
	    
            while ((text = reader.readLine()) != null) 
            {
            	Mail mail = new Mail();
            	mail.fromString(text);
                dataList.add(mail);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace(System.out);
        } catch (IOException e) {
            e.printStackTrace(System.out);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
        }
    }
}
