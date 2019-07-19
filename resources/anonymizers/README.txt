In this directory, you can put jar files containing your own custom
anonymizers. When you do so, don't forget to register them in your config.xml
so that Anonimatron can instantiate them. Examples of an Anonymizer 
and a config below:

This is an example of an anonymizer which returns lower case Strings for
each String passed in:

```java
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
```

If you need an anonymizer with parameters, you can define it like so:

```java
package my.packager;

import java.util.Map;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

public class FixedValueAnonymizer implements ParameterizedAnonymizer {
    @Override
    public Synonym anonymize(Object from, int size, boolean shortlived, Map<String, String> parameters) {
        if (parameters == null || !parameters.containsKey("value")) {
            throw new UnsupportedOperationException("no value");
        }
        return new StringSynonym(getType(),
                (String) from,
                parameters.get("value"),
                shortlived);
    }

    @Override
    public String getType() {
        return "FIXED";
    }
}

```

This is how you add it to your config.xml:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration jdbcurl="jdbc:oracle:thin:@[HOST]:[PORT]:[SID]"
    userid="userid" password="password">
    <anonymizerclass>my.package.ToLowerAnonymizer</anonymizerclass>
    <anonymizerclass>my.package.FixedValueAnonymizer</anonymizerclass>
    <table name="MY_TABLE">
        <column name="MY_COLUMN1" type="TO_LOWER_CASE"/>
        <column name="MY_COLUMN2" type="FIXED">
            <parameter id="value">testValue</parameter>
        </column>
    </table>
</configuration>
```

Have fun experimenting!
