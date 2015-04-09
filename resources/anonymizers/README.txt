In this directory, you can put jar files containing your own custom
anonymizers. When you do so, don't forget to register them in your config.xml
so that Anonimatron can instantiate them. Examples of an Anonymizer 
and a config below:

This is an example of an anonymizer which returns lower case Strings for
each String passed in:

package my.package;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

public class ToLowerAnonymizer implements Anonymizer {

	@Override
	public String getType() {
		return "TO_LOWER_CASE";
	}

	@Override
	public Synonym anonymize(Object from, int size) {
		StringSynonym s = new StringSynonym();
		s.setFrom(from);
		s.setTo(((String)from).toLowerCase());
		return s;
	}
}


This is how you add it to your config.xml:

<?xml version="1.0" encoding="UTF-8"?>
<configuration jdbcurl="jdbc:oracle:thin:@[HOST]:[PORT]:[SID]"
    userid="userid" password="password">
    <anonymizerclass>my.package.ToLowerAnonymizer</anonymizerclass>
    <table name="MY_TABLE">
        <column name="MY_COLUMN" type="TO_LOWER_CASE"/>
    </table>
</configuration>


Have fun experimenting!