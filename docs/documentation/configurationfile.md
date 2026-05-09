# Configuration File

The quickstart shows you the smallest useful configuration file. That is a nice way to get started,
but there is more you can tell Anonimatron to do. This page walks through the XML configuration file
and the options you can use when databases, files, custom anonymizers, and a few special cases come
together.

If you ever get lost, ask Anonimatron to print an example:

```bash
./anonimatron.sh -configexample
```

Once you have a configuration file, run it like this:

```bash
./anonimatron.sh -config config.xml -synonyms synonyms.xml
```

The synonym file is where Anonimatron remembers that value A was replaced with value X. If you use
the same synonym file in the next run, the same input values will get the same replacement values.

## A Bigger Example

Here is a configuration file which shows most of the moving parts. You probably do not need all of
this on day one, but it is useful to see how the pieces fit together.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration jdbcurl="jdbc:mysql://localhost:3306/mydb"
               userid="myuser"
               password="mypassword"
               salt="example-salt">

  <anonymizerclass>my.package.FixedValueAnonymizer</anonymizerclass>
  <filefilterclass>my.package.CsvFileFilter</filefilterclass>

  <table name="userdata" fetchSize="500">
    <column name="firstname" type="ROMAN_NAME" />
    <column name="lastname" type="ELVEN_NAME" />
    <column name="creditcardnr" type="RANDOMDIGITS">
      <parameter id="mask">1111********</parameter>
    </column>
    <column name="session_token" type="STRING" shortlived="true" />

    <discriminator columnname="contact_type" value="email">
      <column name="contact_value" type="EMAIL_ADDRESS" />
    </discriminator>
    <discriminator columnname="contact_type" value="phone">
      <column name="contact_value" type="RANDOMDIGITS" />
    </discriminator>
  </table>

  <file inFile="customers.csv"
        outFile="customers-anonymized.csv"
        reader="com.rolfje.anonimatron.file.CsvFileReader"
        writer="com.rolfje.anonimatron.file.CsvFileWriter"
        encoding="UTF-8">
    <readerParameter id="delimiter">;</readerParameter>
    <writerParameter id="delimiter">;</writerParameter>
    <column name="1" type="ROMAN_NAME" />
    <column name="2" type="EMAIL_ADDRESS" />
  </file>
</configuration>
```

When a configuration contains both `<table>` and `<file>` elements, Anonimatron processes all tables
first and then all files. The same synonym cache is used for both, so a value anonymized in a table
can be replaced consistently in a file later in the same run.

## The Configuration Element

Everything starts with the `<configuration>` element. For database anonymization, this is where the
database connection lives:

```xml
<configuration jdbcurl="jdbc:mysql://localhost:3306/mydb"
               userid="myuser"
               password="mypassword">
```

The `jdbcurl`, `userid`, and `password` attributes are only needed when you anonymize database
tables. You can also provide them on the command line with `-jdbcurl`, `-userid`, and `-password`.
Command line values override the values in the XML file, which is handy when you do not want to put
passwords in a configuration file.

There is one other root attribute worth knowing about: `salt`. When you set it, Anonimatron hashes
the original values in the synonym file using that salt. Keep the salt stable if you want old synonym
files to remain usable across runs.

You can also register extra classes at the top level:

```xml
<anonymizerclass>my.package.FixedValueAnonymizer</anonymizerclass>
<filefilterclass>my.package.CsvFileFilter</filefilterclass>
```

Use `anonymizerclass` for your own anonymizers. Use `filefilterclass` when a file input points to a
directory and you want to decide which files in that directory should be processed.

## Database Tables

To anonymize a table, add a `<table>` element and then list the columns Anonimatron should touch:

```xml
<table name="userdata">
  <column name="firstname" type="ROMAN_NAME" />
  <column name="lastname" type="ELVEN_NAME" />
</table>
```

This tells Anonimatron to read the `userdata` table and replace the values in the `firstname` and
`lastname` columns. The `type` attribute points to an anonymizer type. You can find the built-in
types in the [available anonymizers](anonymizerlist.md) page.

Tables must have a primary key. Anonimatron uses that primary key to update rows in place. This also
means you can not anonymize a primary key column.

For large tables, you can set a JDBC fetch size:

```xml
<table name="userdata" fetchSize="500">
```

Whether this helps depends on the JDBC driver, but it can reduce memory usage for large result sets.
Schema-qualified table names such as `myschema.userdata` are supported when the database exposes the
right schema metadata.

## Files

Anonimatron can anonymize files too. A file configuration tells Anonimatron where to read from, where
to write to, and which reader and writer classes to use:

```xml
<file inFile="customers.csv"
      outFile="customers-anonymized.csv"
      reader="com.rolfje.anonimatron.file.CsvFileReader"
      writer="com.rolfje.anonimatron.file.CsvFileWriter">
  <column name="1" type="ROMAN_NAME" />
  <column name="2" type="EMAIL_ADDRESS" />
</file>
```

The built-in CSV reader does not use header names. It numbers fields from left to right, starting at
1. So `name="1"` means the first field, `name="2"` means the second field, and so on.

CSV files are read and written as UTF-8 by default. If your file uses another encoding, set it with
a Java charset name:

```xml
<file inFile="customers.csv"
      outFile="customers-anonymized.csv"
      reader="com.rolfje.anonimatron.file.CsvFileReader"
      writer="com.rolfje.anonimatron.file.CsvFileWriter"
      encoding="ISO-8859-1">
```

The built-in CSV reader and writer also support a delimiter parameter:

```xml
<readerParameter id="delimiter">;</readerParameter>
<writerParameter id="delimiter">;</writerParameter>
```

If you leave this out, the reader treats comma, semicolon, and tab as delimiters. The writer uses
comma by default. The CSV support is intentionally simple and does not try to be a full spreadsheet
parser.

The `inFile` attribute may point to a single file or a directory. When it points to a directory,
Anonimatron considers the direct child files in that directory. If `outFile` exists as a directory,
output files are written there with their original file names. Anonimatron will refuse to overwrite
an existing output file or use the same file as both input and output.

## Columns

Columns are where you tell Anonimatron what kind of replacement data you want:

```xml
<column name="creditcardnr" type="RANDOMDIGITS" />
```

The most important attributes are:

| Attribute    | Meaning                                                                                    |
| :----------- | :----------------------------------------------------------------------------------------- |
| `name`       | Database column name, or CSV field number for the built-in CSV reader.                     |
| `type`       | Anonymizer type, such as `ROMAN_NAME`, `EMAIL_ADDRESS`, or `RANDOMDIGITS`.                 |
| `size`       | Optional size passed to the anonymizer. For database columns, JDBC metadata supplies this. |
| `shortlived` | Set to `true` when the generated synonym should not be stored in the synonym file.         |

For database columns, `type` may be left out. In that case Anonimatron asks JDBC for the Java class
name of the column and tries to use a default anonymizer mapping. In practice, it is usually clearer
to set the type yourself.

Short-lived columns are useful for values that just need to be filled with plausible junk and do not
need to be stable between runs:

```xml
<column name="session_token" type="STRING" shortlived="true" />
```

Use this with care. Because short-lived synonyms are not stored, repeated runs will generate new
values for the same input.

## Parameters

Some anonymizers accept extra parameters. Parameters are written inside a column:

```xml
<column name="creditcardnr" type="RANDOMDIGITS">
  <parameter id="mask">1111********</parameter>
</column>
```

For `RANDOMDIGITS`, the `mask` parameter tells Anonimatron which characters to keep. Digits in the
mask keep the original character at that position; other positions are replaced by random digits.
So `1111****` keeps the first four characters and replaces the rest.

`RANDOMCHARACTERS` accepts a `characters` parameter:

```xml
<column name="code" type="RANDOMCHARACTERS">
  <parameter id="characters">ABC123</parameter>
</column>
```

This makes Anonimatron generate replacement values using only those characters. Custom anonymizers
can define their own parameters.

## Discriminators

Sometimes one column contains different kinds of data. A `contact_value` column might contain a phone
number for one row and an email address for another. Discriminators handle that case:

```xml
<table name="contacts">
  <column name="contact_value" type="RANDOMDIGITS" />

  <discriminator columnname="contact_type" value="email">
    <column name="contact_value" type="EMAIL_ADDRESS" />
  </discriminator>
</table>
```

Here, `contact_value` is treated as random digits by default. But when `contact_type` is `email`,
Anonimatron uses the `EMAIL_ADDRESS` anonymizer instead. If the discriminator has no `value`
attribute, it matches `null`.

Discriminators are applied to database tables. The XML mapping also accepts them under `<file>`, but
the current file anonymization flow does not apply file-level discriminators.

## Custom Classes

Anonimatron can load your own anonymizers. Put the jar on the Anonimatron classpath. In the binary
distribution, the usual place is the `anonymizers` directory. Then register the class:

```xml
<configuration>
  <anonymizerclass>my.package.ToLowerAnonymizer</anonymizerclass>
  <table name="userdata">
    <column name="firstname" type="TO_LOWER_CASE" />
  </table>
</configuration>
```

The important part is that the anonymizer returns a type, and that the column uses that same type.

You can also plug in your own file readers and writers. Implement `RecordReader` or `RecordWriter`.
If your reader or writer needs configuration, implement `ParameterizedRecordReader` or
`ParameterizedRecordWriter` and use `readerParameter` or `writerParameter` in the XML.

## Command Line Options

The XML file does not contain everything. These runtime options are provided on the command line:

| Option                             | What it does                                                                                            |
| :--------------------------------- | :------------------------------------------------------------------------------------------------------ |
| `-config config.xml`               | Reads the XML configuration file.                                                                       |
| `-synonyms synonyms.xml`           | Reads and writes the synonym file. If it does not exist, it will be created.                            |
| `-dryrun`                          | Runs database anonymization without writing changes to the database. It can still write a synonym file. |
| `-jdbcurl`, `-userid`, `-password` | Override the matching XML attributes.                                                                   |
| `-configexample`                   | Prints supported JDBC URL formats and a generated example configuration.                                |

When building your configuration file, start small, anonymize one or two columns, inspect the
result, and then add the rest. It is much easier to fix a tiny configuration than a giant one that
tries to anonymize everything at once.
