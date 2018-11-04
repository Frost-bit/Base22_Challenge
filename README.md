# Base22_Challenge
Backend challenge provided by Base22

## Dependencies
* JCommander (for parsing arguments)
* JSoup (for web scraping)

## Compile jar with dependencies
mvn clean compile assembly:single

## Usage
java -cp target/backend-challenge-base22-1.0-jar-with-dependencies.jar com.base22.challenge.Base22Challenge -inputFileName ../base22/urls.txt -outputFileName ../base22/scrapped.csv -cleanHTML

## Reading the CSV
Open any editor for csv and select as delimiter "  " (double space, I chose this so that the body of the HTML will appear in one column withput any problem).
