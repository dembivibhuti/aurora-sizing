'''
--- Usage ---
python <thisScriptName> [?BucketCsv] [?InitialSizeCsv] [?OutputFile]

Defaults : look at getFileName method

--- Info --- 
A 'Size CSV' is a CSV file with schema:  [ClassId, Size, Count]
Such CSV can be used to create 'Count' number records of 'Size' in aurora db for given 'ClassId'.

For this scripts will accept two files:
	1. Intial 'Size CSV' .. Lets say InitialSizeCsv
	2. Bucket CSV: [ ClassId, AverageSize, DistinctSizesCount, PeakRecordsCount, TotalRecords, StdDev ]

The BucketCSV needs to generate 'Size CSV' (as originally, it was created from a similar 'Size CSV'), lets name that SizeFromBucketCsv

The objective is to produce final 'FinalSizeCsv' after combininng InitialSizeCsv and SizeFromBucketCsv
'''
import csv
import sys
import random

def getFileName(type):
	if(type.startswith('B')): 
		return sys.argv[1] if len(sys.argv)>=2 else "./testBuckets.csv";
	if(type.startswith('I')): 
		return sys.argv[2] if len(sys.argv)>=3 else "./testInitialSizes.csv";
	if(type.startswith('F')): 
		return sys.argv[3] if len(sys.argv)>=4 else "./testfinalOutput.csv";
	throw("Unknown filetype" );

def maxSizeAllowed(classId):
	if classId != 4020:
		return 32711
	else:
		return 10e6;

def convertBucketToSizes(bucketTable, excludes, maxSize):
	outputTable = []; #list of {ClassId, Size, Count}
	for row in bucketTable:
		asize = row[AverageSize]
		while(asize in excludes):
			asize += 1
		outputTable.append( [asize, row['PeakRecordsCount'] ] )
		excludes.add(asize);
		
		recordsToAdd = row['TotalRecords'] - row['PeakRecordsCount']
		distinctsRemaining = row['DistinctSizesCount'] - 1
		avgFill = recordsToAdd / distinctsRemaining;
		while( recordsToAdd > 0 and distinctsRemaining > 0):
			deviation = random.gauss(0, row['StdDev'])
			deviation = round(abs(deviation)) if( deviation > 0 ) else -round(abs(deviation));
			step = 1

			cx = row['AverageSize'] + deviation
			if( cx < 1 or cx > maxSize ):  #disallow negative sizes, huge sizes
				continue;

			while( cx in excludes and cx < maxSize ):
				cx += step

			if distinctsRemaining > 1:
				cy = min(
					round( ( avgFill + 2*random.random()*avgFill )/2 ), #gives you random point between 0.5 to 1.5 stdDev
					row['PeakRecordsCount'], #never cross the peak
					recordsToAdd - distinctsRemaining + 1
				);
				cy = max(1,cy)  ##defensive, dont let cy go below 1
			else:
				cy = recordsToAdd

			excludes.add(cx) #dont repeat this point again
			outputTable.append([cx, cy])
			distinctsRemaining -= 1;
			recordsToAdd -= cy

	return(outputTable);

def produceFinalCsv(initialSizeTable, bucketTable):
	pass;

def readCsvToTable(csvFileName):
	table = []
	with open(csvFileName) as csvfile:
		readCSV = csv.reader(csvfile, delimiter=',')
		head = next(readCSV)
		#print(".....", head)
		for row in readCSV:
			#print(row)
			row = [float(x) if '.' in x else int(x) for x in row]
			table += [ dict(zip(head,row)) ]
	
	return table;

def readCsvsToTable():
	bucketCsv = getFileName('Buckets');
	initialSizeCsv = getFileName('InitialSizeCsv');
	bucketTable = readCsvToTable(bucketCsv);
	initialSizeTable = readCsvToTable(initialSizeCsv);
	return( [bucketTable, initialSizeTable])

def getClasses(bucketTable, initialSizeTable):
	return( list( set( list( set( [ x['ClassId'] for x in bucketTable ] ) ) + 
		list( set( [ x['ClassId'] for x in initialSizeTable ] ) ) ) ) );

def getClassTable(bucketTable, initialSizeTable, classId):
	return(
		[
			[ x for x in bucketTable if x['ClassId'] == classId ],
			[ x for x in initialSizeTable if x['ClassId'] == classId ]
		]
	)		


def combineSizes(bucketGeneratedSizes, initialSizeTable):
	#initialSizeTable is list of dict, make it list of list
	initialSizes = [[x['Size'], x['Count'] ] for x in initialSizeTable]
	combinedSizes = initialSizes + bucketGeneratedSizes
	xvals = list(set( [x[0] for x in combinedSizes] ));
	xvals.sort();
	yvals = [ sum(cs[1] for cs in combinedSizes if cs[0]==x) for x in xvals]   
	return list(zip(xvals, yvals))

def writeOutput(finalSizes):
	f = open(getFileName('FinalOutput'), "w")
	f.write("ClassId,Size,Count\n");
	for sizeInfo in finalSizes:
		sizeStr = ",".join([str(s) for s in sizeInfo]) + "\n";
		f.write(sizeStr)
	f.close()

def main():
	[bucketTable, initialSizeTable] = readCsvsToTable()
	classIds = getClasses(bucketTable, initialSizeTable)
	finalSizes = []
	for classId in classIds:
		random.seed(1)
		[cBucket, cInitial] = getClassTable(bucketTable, initialSizeTable, classId)
		excludes = set([ x['Size'] for x in initialSizeTable ]);
		bucketGeneratedSizes = convertBucketToSizes( cBucket, excludes, maxSizeAllowed(classId));
		sizesForThisClass = combineSizes(bucketGeneratedSizes,cInitial)
		sizesForThisClass = [ [classId] + list(x) for x in sizesForThisClass]
		finalSizes += sizesForThisClass
	writeOutput(finalSizes)

main();	