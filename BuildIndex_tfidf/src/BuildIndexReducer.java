import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class BuildIndexReducer extends MapReduceBase implements
		Reducer<Text, Text, Text, Text> {
	private Text postinglist = new Text();

	public void reduce(Text key, Iterator<Text> values,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		HashMap<String, List<String>> uniquePos = new HashMap<String, List<String>>();

		for (; values.hasNext();) {
			String posting = values.next().toString();
			String docId = posting.split(",")[0];
			String offset = posting.split(",")[1];
			if (uniquePos.containsKey(docId)) {
				// System.out.println(uniquePos.get(docId));
				uniquePos.get(docId).add(offset);
			} else {
				List<String> l = new ArrayList<String>();
				l.add(offset);
				uniquePos.put(docId, l);
			}
		}

		postinglist.set(new Text(StringUtils.join(uniquePos.entrySet(), "|")));
		output.collect(key, postinglist);
	}

}