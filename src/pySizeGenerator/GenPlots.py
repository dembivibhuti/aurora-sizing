'''
Usage
python <script> [?inputCsv] > <htmlFile>

open HTML in chrome
'''

import csv
import sys

htmlHead = '''
<html>
   <head>
      <title>Aurora Records</title>
      <script src = "https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js">
      </script>
      <script src = "https://code.highcharts.com/highcharts.js"></script> 
   </head>
   <body>
'''

htmlTail = '''
   </body>
</html>
'''

plotTemplate = '''
      <div id = "container<<ClassName>>" style = "width: 550px; height: 400px; margin: 0 auto"></div>
      <script language = "JavaScript">
         $(document).ready(function() {
            var title = { text: 'Records for Class:  <<ClassName>>' };
            var xAxis = { title: { text: 'Size' } };
            var yAxis = {
               title: { text: 'Count' },
               plotLines: [{ value: 0, width: 1, color: '#808080' }]
            };   
            var series =  [{
                  name: 'Simulated data',
                  data: [
                     <<DATA>>
                  ]
               }
            ];
            var json = {
               'title' :  title,
               'xAxis' :  xAxis,
               'yAxis' :  yAxis,
               'series':  series
            }
            $('#container<<ClassName>>').highcharts(json);
         });
      </script>
'''

def loadCsvData():
   csvFileName = sys.argv[1] if(len(sys.argv)>=2) else "./finalOutput.csv"
   table = []
   with open(csvFileName) as csvfile:
      readCSV = csv.reader(csvfile, delimiter=',')
      head = next(readCSV)
      for row in readCSV:
         row = [float(x) if '.' in x else int(x) for x in row]
         table += [ dict(zip(head,row)) ]
   return table;

def generatePlot(classId, data):
   global plotTemplate
   plotString = plotTemplate
   plotString = plotString.replace( '<<ClassName>>', str(classId) )
   dataStr = ",".join( [ "[" + str(d[0]) + "," + str(d[1]) + "]" for d in data] )
   plotString = plotString.replace('<<DATA>>', dataStr)
   return plotString

def generateHTML():
   fullData = loadCsvData();
   classIds = list(set([x['ClassId'] for x in fullData] ) );
   classIds.sort()
   global htmlHead;
   global htmlTail 

   htmlStr = htmlHead;
   for classId in classIds:
      classData = [ [x['Size'], x['Count']] for x in fullData if x['ClassId'] == classId]
      htmlStr += generatePlot(classId, classData)

   htmlStr += htmlTail;
   print(htmlStr);

generateHTML()
