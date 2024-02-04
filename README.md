# CSV Util from Open Csv
The CSV Utilities offers a comprehensive set of tools for seamless handling and manipulation of CSV data within Java applications. It includes configurable CSV parsing settings, customizable column mapping strategies, and flexible data transformation capabilities. Designed for ease of integration and usage, this utility simplifies common CSV processing tasks and enhances productivity in data-centric Java projects.

Certainly! Here's a usage guide in Markdown format for the `CsvReader` class:

# CsvReader Usage Guide

The `CsvReader` utility class provides a convenient way to read data from CSV files and transform them into Java objects. This guide will walk you through the basic steps of using `CsvReader` with explanations for each step.

## 1. Define a Java Class

Start by defining a Java class that represents the structure of your CSV records. Annotate the fields with OpenCSV annotations like `@CsvBindByName` to specify the mapping between CSV columns and Java fields.

```java
@Data
public class Person {
    @CsvBindByName(column = "ID")
    private int id;

    @CsvBindByName(column = "Name")
    private String name;

    @CsvBindByName(column = "Age")
    private int age;
}
```

## 2. Instantiate CsvReader

Create an instance of `CsvReader` for the specified class type (e.g., `Person`). This class type is the target type to which each CSV record will be mapped.

```java
CsvReader<Person> csvReader = CsvReader.of(Person.class);
```

## 3. Configure CsvReader (Optional)

You can configure `CsvReader` with custom settings using the `config` method. For example, you can set a custom separator or specify whether the CSV file has a header.

```java
csvReader = csvReader.config(config -> config
        .setSeparator(';')
        .setHasHeader(true));
```

## 4. Read CSV Data

Use the `readCSV` method to read data from the CSV file into a list of Java objects. This method may throw a `CsvReaderException` if any errors occur during reading or mapping.

```java
String filePath = "path/to/your/file.csv";

try {
    List<Person> personList = csvReader.readCSV(filePath);

    // Now 'personList' contains Java objects with data from the CSV file
    for (Person person : personList) {
        System.out.println(person);
    }
} catch (CsvReaderException e) {
    // Handle any exceptions that may occur during CSV reading or mapping
    e.printStackTrace();
}
```

## 5. Additional Methods

- **Read CSV from Byte Array**: Use `readCSV(byte[] content)` to read CSV data from a byte array.
- **Read CSV from Base64 String**: Use `readCSVFromBase64(String base64Content)` to read CSV data from a Base64 encoded string.
- **Read CSV from Classpath**: Use `readCSVFromClasspath(String resourcePath)` to read CSV data from a file in the classpath.

Feel free to adjust the code based on your specific use case and CSV file structure. The `CsvReader` class provides flexibility and various configuration options to suit different scenarios.

Certainly! Here's a more detailed explanation of the `CsvWriter` usage guide:

### Step 1: Define a Java Class

Define a Java class that represents the structure of your CSV records. Annotate the fields with the appropriate OpenCSV annotations.

```java
@Data
public class Person {
    private int id;
    private String name;
    private int age;
}
```

### Step 2: Create Data for CSV

Create a list of Java objects with the data you want to write into the CSV file. In this example, we have a list of `Person` objects.

```java
List<Person> personList = Arrays.asList(
        new Person(1, "John Doe", 25),
        new Person(2, "Jane Smith", 30),
        new Person(3, "Bob Johnson", 22)
);
```

### Step 3: Specify File Path or Resource Path

Specify the file path or resource path where you want to write the CSV data.

```java
String filePath = "path/to/your/output/file.csv";
```

### Step 4: Create CsvWriter Instance

Instantiate a `CsvWriter` object for the specified class type (e.g., `Person`).

```java
CsvWriter<Person> csvWriter = CsvWriter.of(Person.class);
```

### Step 5: Configure CsvWriter (Optional)

Use the `config` method to customize CsvWriter settings. This step is optional, and you can skip it if you are using the default settings.

```java
csvWriter = csvWriter.config(config -> config
        .setSeparator(';')
        .setQuoteChar('"')
        .setEscapeChar('\\')
        .setLineEnd("\r\n"));
```

### Step 6: Write CSV Data

Use various methods provided by `CsvWriter` to write CSV data to different destinations:

#### Write CSV to a File

```java
csvWriter.writeCSV(filePath, personList);
```

#### Write CSV to an Output Stream

```java
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
csvWriter.writeCSV(outputStream, personList);
```

#### Get CSV as a Byte Array

```java
byte[] csvByteArray = csvWriter.writeCSV(personList);
```

#### Get CSV as a Base64 Encoded String

```java
String base64Csv = csvWriter.writeCSVToBase64(personList);
```

### Step 7: Handle Exceptions

Wrap the CSV writing code in a try-catch block to handle any exceptions that may occur during the writing process.

```java
try {
    // CSV writing code here
} catch (CsvWriterException e) {
    // Handle exceptions, e.g., log, display an error message, etc.
    e.printStackTrace();
}
```

This detailed guide walks you through each step of using the `CsvWriter` class to write CSV data from Java objects. Adjust the code based on your specific use case and preferences.

**Note:** *Currently, this CSV utility is implemented for personal use case purposes.*
