import java.io.IOException;
import java.util.*;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class InvertedIndexBigrams {

    public static class TokenizerMapper extends Mapper<Object, Text, Text, Text> {

        private Text word = new Text();
        private Text documentID = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] pairs = value.toString().split("\t", 2);
            documentID.set(pairs[0]);
            String document = pairs[1].replaceAll("[^a-zA-Z]+", " ").toLowerCase(); // convert special characters to white space & convert everything into lower case
            StringTokenizer itr = new StringTokenizer(document);

            String firstToken = null;
            while (itr.hasMoreTokens()) {
                String secondToken = itr.nextToken();
                if (firstToken != null) {
                    word.set(firstToken + " " + secondToken);
                    context.write(word, documentID);
                }
                firstToken = secondToken;
            }
        }
    }

    public static class IntSumReducer extends Reducer<Text, Text, Text, Text> {

        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            HashMap<String, Integer> map = new HashMap<>();
            for (Text value : values) {
                String documentId = value.toString();
                map.put(documentId, map.getOrDefault(documentId, 0) + 1);
            }
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Integer> entries : map.entrySet()) {
                sb.append(entries.getKey()).append(":").append(entries.getValue()).append(" ");
            }
            context.write(key, new Text(sb.toString()));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(InvertedIndexBigrams.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setReducerClass(IntSumReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}