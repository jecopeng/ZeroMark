package ZeroMark;

import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

public class attack {
    public static void main(String[] args) throws CsvValidationException, SQLException, IOException {
        Data d = new Data("database\\queaterlywages2000.csv",'a');//for QCEW
        //Data d = new Data("database\\geography.csv",'a');//for geography
        //Data tmp = attack.deleteAttackwithOutSQL(d);
        //Data tmp = attack.insertAttackWithoutSQL(d);
       // Util.toCsv("attacked database\\data.csv",tmp);//Store the attacked dataset in data.csv.(for deletion attack and insertion attack)

        //attack.alterattackWithFre(d);
        attack.altAttackwithOutSQL(d);
        //attack.verticalModificationAttack(d,2);
        Util.toCsv("attacked database\\data.csv",d);//Store the attacked dataset in data.csv.(for modification attack)
    }
    /**
     * Select a certain proportion of tuples
     * In the deletion attack, the deletion proportion is rate.
     * In the modification attack, the modification proportion is 1-rate.
     * @param d
     * @return
     */
    static HashSet<Integer> select_tuple(Data d){
        //In deletion attacks, the proportion indicated for deletion in the deletion attack,
        //In modification attacks, the proportion indicated for not modified in the modification attack
        double rate = 0.2;

        //tuple_num is the number of tuples retained in the deletion attack and the number of tuples modified in the modification attack.
        int tuple_num = (int)((1-rate)*d.d.get(0).size());

        HashSet<Integer> set = new HashSet<>();
        Random random = new Random();
        while(set.size()<tuple_num){
            int temp = random.nextInt(d.d.get(0).size());
            set.add(temp);
        }
        return set;
    }

    /**
     * Simulate a deletion attack by deleting all tuples except for the selected tuples
     * @param d
     * @return the dataset containing only the remaining selected tuples
     * @throws SQLException
     * @throws CsvValidationException
     * @throws IOException
     */
    public static Data deleteAttackwithOutSQL(Data d) throws SQLException, CsvValidationException, IOException {
        HashSet<Integer> set = select_tuple(d);
        Data datatmp = new Data(d.d.size(),set.size());
        datatmp.name = d.name;
        for(int i : set){//Delete all tuples except for the selected tuples
            for(int j = 0;j<d.d.size();j++){
                datatmp.d.get(j).add(d.d.get(j).get(i));//第j个属性修改为第i个元组的
            }
        }
        return datatmp;
    }

    /**
     * Randomly generate a certain proportion of tuples and insert them into the original dataset to simulate an insertion attack.
     * In the experiment, all inserted data are randomly generated, compromising data availability.
     * @param d
     * @throws SQLException
     */
    public static Data insertAttackWithoutSQL(Data d) throws SQLException {
        double rate = 1.0;
        int tmp = (int)(d.d.get(0).size() * rate);//The number of tuples to be inserted
        Data datatmp = new Data(d.d.size(),tmp+d.d.get(0).size());
        Random random = new Random();
        for(int i=0;i<d.d.get(0).size();i++){//Copy the original database into the attacked database
            for(int j = 0;j<d.d.size();j++){
                datatmp.d.get(j).add(d.d.get(j).get(i));
            }
        }
        for(int i=0;i<tmp;i++){//Generate tuples composed of random values for insertion
            for(int j = 0;j<d.d.size();j++){
                String t = random.nextDouble()*10000000 +"";
                datatmp.d.get(j).add(t);
            }
        }
        datatmp.name = d.name;
        return datatmp;
    }

    /**
     * High-frequency modification attack: Prioritize modifying the values that occur frequently in the attributes.
     * In the experiment, all inserted data are randomly generated, compromising data availability.
     * @param d
     */
    public static void alterattackWithFre(Data d){
        d.compute_count();
        d.sort_count();//Sort all unique values for each attribute by their frequency of occurrence.
        double r = 0.8;
        Random random = new Random();
        for(int i = 0;i<d.d.size();i++){//Prioritize modifying unique values with higher frequencies
            int count = 0;//Count the modified values until the attack ratio is reached
            int tmp = 0;
            while((double)count/d.d.get(i).size()<r){
                //Ensure that identical attribute values remain the same after modification to maintain the distribution characteristics
                count += d.entryList.get(i).get(tmp).getValue().size();
                String t = random.nextDouble()*10000000+"";
                for (int j : d.entryList.get(i).get(tmp).getValue()) {//
                    d.d.get(i).set(j,t);
                }
                tmp++;
            }
        }
    }

    /**
     * Modification attack: randomly select a certain proportion of tuples for modification
     * @param d
     * @throws SQLException
     * @throws CsvValidationException
     * @throws IOException
     */
    public static void altAttackwithOutSQL(Data d) throws SQLException, CsvValidationException, IOException {
        HashSet<Integer> set = select_tuple(d);//The tuples to be modified
        Random random = new Random();
        for(int i : set){
            for(int j = 0;j<d.d.size();j++){
                d.d.get(j).set(i,random.nextDouble()*10000000+"");
            }
        }
        return;
    }

    /**
     *
     * @param d
     * @param num The number of deleted attributes
     */
    public static void verticalModificationAttack(Data d, int num){
        Random random = new Random();
        for(int i = 0;i<num;i++){
            int tmp = random.nextInt(d.d.size());
            d.d.remove(tmp);
            d.name.remove(tmp);
        }
    }
}
