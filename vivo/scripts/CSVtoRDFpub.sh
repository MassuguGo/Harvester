#!/bin/bash

#Copyright (c) 2010-2011 VIVO Harvester Team. For full list of contributors, please see the AUTHORS file provided.
#All rights reserved.
#This program and the accompanying materials are made available under the terms of the new BSD license which accompanies this distribution, and is available at http://www.opensource.org/licenses/bsd-license.html

# Exit on first error
set -e

# Set working directory
#HARVESTERDIR=`dirname "$(cd "${0%/*}" 2>/dev/null; echo "$PWD"/"${0##*/}")"`
#HARVESTERDIR=$(cd $HARVESTERDIR; cd ..; pwd)
HARVESTERDIR=${WORKING_DIRECTORY} #replaced by servlet
cd $HARVESTERDIR

HARVESTER_TASK=csvGrant

if [ -f vivo/scripts/env ]; then
  . vivo/scripts/env
else
  exit 1
fi
echo "Full Logging in $HARVESTER_TASK_DATE.log"

# Setting variables for cleaner script lines.
#Base data directory
BASEDIR=${HARVESTED_DATA_PATH} #this gets replaced by servlet
PREVHARVDBURLBASE="jdbc:h2:${HARVESTED_DATA_PATH}prevHarv/" #GLOBAL_HARVESTED_DATA_RELATIVE_PATH is replaced by server

#data directories
RAWCSVDIR=$BASEDIR/csv
RAWRHDIR=$BASEDIR/rh-raw
RDFRHDIR=$BASEDIR/rh-rdf
MODELDIR=$BASEDIR/model
SCOREDATADIR=$BASEDIR/score-data

#model database urls
RAWCSVDBURL=jdbc:h2:$RAWCSVDIR/store
RAWRHDBURL=jdbc:h2:$RAWRHDIR/store
RDFRHDBURL=jdbc:h2:$RDFRHDIR/store
MODELDBURL=jdbc:h2:$MODELDIR/store
SCOREDATADBURL=jdbc:h2:$SCOREDATADIR/store
MATCHEDDBURL=jdbc:h2:$SCOREDATADIR/match

#model names
MODELNAME=csvTempTransfer
SCOREDATANAME=csvScoreData
MATCHEDNAME=csvTempMatch

#temporary copy directory
TEMPCOPYDIR=$BASEDIR/temp-copy

#scoring data models
SCOREINPUT="-i $H2MODEL -ImodelName=$MODELNAME -IdbUrl=$MODELDBURL -IcheckEmpty=$CHECKEMPTY"
SCOREDATA="-s $H2MODEL -SmodelName=$SCOREDATANAME -SdbUrl=$SCOREDATADBURL -ScheckEmpty=$CHECKEMPTY"
SCOREMODELS="$SCOREINPUT -v $VIVOCONFIG -VcheckEmpty=$CHECKEMPTY $SCOREDATA -t $TEMPCOPYDIR -b $SCOREBATCHSIZE"
MATCHOUTPUT="-o $H2MODEL -OmodelName=$MATCHEDNAME -OdbUrl=$MATCHEDDBURL -OcheckEmpty=$CHECKEMPTY"

#Changenamespace settings
CNFLAGS="$SCOREINPUT -v $VIVOCONFIG -n $NAMESPACE"

#The equality test algorithm
EQTEST="org.vivoweb.harvester.score.algorithm.EqualityTest"

#matching properties
GRANTIDNUM="http://vivoweb.org/ontology/score#grantID"
RDFTYPE="http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
RDFSLABEL="http://www.w3.org/2000/01/rdf-schema#label"
PERSONIDNUM="http://vivoweb.org/ontology/score#personID"
DEPTIDNUM="http://vivoweb.org/ontology/score#deptID"
ROLEIN="http://vivoweb.org/ontology/core#roleIn"
PIROLEOF="http://vivoweb.org/ontology/core#principalInvestigatorRoleOf"
COPIROLEOF="http://vivoweb.org/ontology/core#co-PrincipalInvestigatorRoleOf"
DATETIME="http://vivoweb.org/ontology/core#dateTime"

BASEURI="http://vivoweb.org/harvest/csvfile/"

ADMINNAME="defaultAdmin"
ADMINPASS="vitro123"

#clear old fetches
rm -rf $RAWRHDIR
rm -rf $RAWCSVDIR

#CSVFILE="files/granttemplatetest.csv"
XSLFILE="vivo/datamaps/csv-grant-to-vivo.xsl"

# Execute Fetch
#$CSVtoRDF -o $TFRH -O fileDir=$RAWRHDIR -i $CSVFILE

#I=0
for CURRENT_FILE in ${UPLOADS_FOLDER}* #UPLOADS_FOLDER variable gets replaced by servlet
do
#  $CSVtoRDF -o $TFRH -O fileDir=$RAWRHDIR -i "$CURRENT_FILE"

	$CSVtoJDBC -i "$CURRENT_FILE" -d "org.h2.Driver" -c $RAWCSVDBURL -u "sa" -p "" -t "CSV"
#	$CSVtoJDBC -i "$CURRENT_FILE" -d "org.h2.Driver" -c $RAWCSVDBURL -u "sa" -p "" -t "CSV2"

#	$JDBCFetch -d "org.h2.Driver" -c $RAWCSVDBURL -u "sa" -p "" -o $TFRH -O fileDir=$RAWRHDIR -n "null"

#	I=`expr $I + 1`
done
$JDBCFetch -d "org.h2.Driver" -c $RAWCSVDBURL -u "sa" -p "" -o $TFRH -O fileDir=$RAWRHDIR -n "null"


# backup fetch
#BACKRAW="raw"
#backup-path $RAWRHDIR $BACKRAW
# uncomment to restore previous fetch
#restore-path $RAWRHDIR $BACKRAW

# clear old translates
rm -rf $RDFRHDIR

# Execute Translate
$XSLTranslator -i $TFRH -IfileDir=$RAWRHDIR -o $TFRH -OfileDir=$RDFRHDIR -x $XSLFILE

# backup translate
#BACKRDF="rdf"
#backup-path $RDFRHDIR $BACKRDF
# uncomment to restore previous translate
#restore-path $RDFRHDIR $BACKRDF

# Clear old H2 transfer model
rm -rf $MODELDIR

# Execute Transfer to import from record handler into local temp model
$Transfer -o $H2MODEL -OmodelName=$MODELNAME -OdbUrl=$MODELDBURL -s $TFRH -SfileDir=$RDFRHDIR -n $NAMESPACE

$Transfer -i $H2MODEL -ImodelName=$MODELNAME -IdbUrl=$MODELDBURL -d $BASEDIR/transfer.rdf.xml

# backup H2 transfer Model
#BACKMODEL="model"
#backup-path $MODELDIR $BACKMODEL
# uncomment to restore previous H2 transfer Model
#restore-path $MODELDIR $BACKMODEL

#clear score model for next batch.
rm -rf $SCOREDATADIR

#smushes in-place(-r) on the Grant id THEN on the person ID  then deptID
$Smush $SCOREINPUT -P $GRANTIDNUM -P $PERSONIDNUM -P $DEPTIDNUM -P $DATETIME -n ${BASEURI} -r

$Transfer -i $H2MODEL -ImodelName=$MODELNAME -IdbUrl=$MODELDBURL -d $BASEDIR/smushed.rdf.xml

# Execute score to match with existing VIVO
# The -n flag value is determined by the XLST file
# The -A -W -F & -P flags need to be internally consistent per call

# Scoring of Grants on GrantNumber
$Score $SCOREMODELS -AGrantNumber=$EQTEST -WGrantNumber=1.0 -FGrantNumber=$GRANTIDNUM -PGrantNumber=$GRANTIDNUM -n ${BASEURI}grant/

# Scoring of people on PERSONIDNUM
$Score $SCOREMODELS -Aufid=$EQTEST -Wufid=1.0 -Fufid=$PERSONIDNUM -Pufid=$PERSONIDNUM -n ${BASEURI}person/


$Smush $SCOREINPUT -P $DEPTIDNUM -n ${BASEURI}org/ -r
# Scoring of orgs on DeptID
$Score $SCOREMODELS -AdeptID=$EQTEST -WdeptID=1.0 -FdeptID=$DEPTIDNUM -PdeptID=$DEPTIDNUM -n ${BASEURI}org/


$Smush $SCOREINPUT -P $RDFSLABEL -n ${BASEURI}sponsor/ -r
# Scoring sponsors by labels
$Score $SCOREMODELS -Alabel=$EQTEST -Wlabel=1.0 -Flabel=$RDFSLABEL -Plabel=$RDFSLABEL -n ${BASEURI}sponsor/

# Find matches using scores and rename nodes to matching uri
$Match $SCOREINPUT $SCOREDATA -b $SCOREBATCHSIZE -t 1.0 -r

rm -rf $SCOREDATADIR
rm -rf $TEMPCOPYDIR

# Scoring of PI Roles
PIURI="-Aperson=$EQTEST -Wperson=0.5 -Fperson=$PIROLEOF -Pperson=$PIROLEOF"
GRANTURI="-Agrant=$EQTEST -Wgrant=0.5 -Fgrant=$ROLEIN -Pgrant=$ROLEIN"
$Score $SCOREMODELS $PIURI $GRANTURI -n ${BASEURI}piRole/

# Scoring of coPI Roles
COPIURI="-Aperson=$EQTEST -Wperson=0.5 -Fperson=$COPIROLEOF -Pperson=$COPIROLEOF"
$Score $SCOREMODELS $COPIURI $GRANTURI -n ${BASEURI}coPiRole/

# Scoring of DateTime Starts and ends
$Score $SCOREMODELS -Adate=$EQTEST -Wdate=1.0 -Fdate=$DATETIME -Pdate=$DATETIME -n ${BASEURI}timeInterval/

# Find matches using scores and rename nodes to matching uri
$Match $SCOREINPUT $SCOREDATA -b $SCOREBATCHSIZE -t 1.0 -r

rm -rf $SCOREDATADIR
rm -rf $TEMPCOPYDIR

STARTTIME="http://vivoweb.org/ontology/core#start"
ENDTIME="http://vivoweb.org/ontology/core#end"

# Scoring of DateTimeInterval
$Score $SCOREMODELS -Asdate=$EQTEST -Wsdate=0.5 -Fsdate=$STARTTIME -Psdate=$STARTTIME -Aedate=$EQTEST -Wedate=0.5 -Fedate=$ENDTIME -Pedate=$ENDTIME -n ${BASEURI}timeInterval/

# Find matches using scores and rename nodes to matching uri
$Match $SCOREINPUT $SCOREDATA -b $SCOREBATCHSIZE -t 1.0 -r

$Transfer -i $H2MODEL -ImodelName=$MODELNAME -IdbUrl=$MODELDBURL -d $BASEDIR/matched.rdf.xml
# Execute ChangeNamespace to get grants into current namespace
# the -o flag value is determined by the XSLT used to translate the data
$ChangeNamespace $CNFLAGS -u ${BASEURI}grant/

# Execute ChangeNamespace to get orgs into current namespace
# the -o flag value is determined by the XSLT used to translate the data
$ChangeNamespace $CNFLAGS -u ${BASEURI}org/

# Execute ChangeNamespace to get sponsors into current namespace
# the -o flag value is determined by the XSLT used to translate the data
$ChangeNamespace $CNFLAGS -u ${BASEURI}sponsor/

# Execute ChangeNamespace to get people into current namespace
# the -o flag value is determined by the XSLT used to translate the data
$ChangeNamespace $CNFLAGS -u ${BASEURI}person/

# Execute ChangeNamespace to get PI roles into current namespace
# the -o flag value is determined by the XSLT used to translate the data
$ChangeNamespace $CNFLAGS -u ${BASEURI}piRole/

# Execute ChangeNamespace to get co-PI roles into current namespace
# the -o flag value is determined by the XSLT used to translate the data
$ChangeNamespace $CNFLAGS -u ${BASEURI}coPiRole/

# Execute ChangeNamespace to get co-PI roles into current namespace
# the -o flag value is determined by the XSLT used to translate the data
$ChangeNamespace $CNFLAGS -u ${BASEURI}timeInterval/


$Transfer -i $H2MODEL -ImodelName=$MODELNAME -IdbUrl=$MODELDBURL -d $BASEDIR/changed.rdf.xml

# backup H2 matched Model
#BACKMATCHED="matched"
#backup-path $MODELDIR $BACKMATCHED
# uncomment to restore previous H2 matched Model
#restore-path $MODELDIR $BACKMATCHED

# Backup pretransfer vivo database, symlink latest to latest.sql
#BACKPREDB="pretransfer"
#backup-mysqldb $BACKPREDB
# uncomment to restore pretransfer vivo database
#restore-mysqldb $BACKPREDB

PREVHARVESTMODEL="http://vivoweb.org/ingest/dsr"
ADDFILE="$BASEDIR/additions.rdf.xml"
SUBFILE="$BASEDIR/subtractions.rdf.xml"

# Find Subtractions
$Diff -m $H2MODEL -MdbUrl=${PREVHARVDBURLBASE}${HARVESTER_TASK}/store -McheckEmpty=$CHECKEMPTY -MmodelName=$PREVHARVESTMODEL -s $H2MODEL -ScheckEmpty=$CHECKEMPTY -SdbUrl=$MODELDBURL -SmodelName=$MODELNAME -d $SUBFILE
# Find Additions
$Diff -m $H2MODEL -McheckEmpty=$CHECKEMPTY -MdbUrl=$MODELDBURL -MmodelName=$MODELNAME -s $H2MODEL -ScheckEmpty=$CHECKEMPTY -SdbUrl=${PREVHARVDBURLBASE}${HARVESTER_TASK}/store -SmodelName=$PREVHARVESTMODEL  -d $ADDFILE

# Backup adds and subs
#backup-file $ADDFILE adds.rdf.xml
#backup-file $SUBFILE subs.rdf.xml
#restore-file $ADDFILE adds.rdf.xml
#restore-file $SUBFILE subs.rdf.xml

# Apply Subtractions to Previous model
$Transfer -o $H2MODEL -OdbUrl=${PREVHARVDBURLBASE}${HARVESTER_TASK}/store -OcheckEmpty=$CHECKEMPTY -OmodelName=$PREVHARVESTMODEL -r $SUBFILE -m
# Apply Additions to Previous model
$Transfer -o $H2MODEL -OdbUrl=${PREVHARVDBURLBASE}${HARVESTER_TASK}/store -OcheckEmpty=$CHECKEMPTY -OmodelName=$PREVHARVESTMODEL -r $ADDFILE
# Apply Subtractions to VIVO
$Transfer -o $VIVOCONFIG -OcheckEmpty=$CHECKEMPTY -r $SUBFILE -m
# Apply Additions to VIVO
$Transfer -o $VIVOCONFIG -OcheckEmpty=$CHECKEMPTY -r $ADDFILE

rm -rf $TEMPCOPYDIR

# Backup posttransfer vivo database, symlink latest to latest.sql
#BACKPOSTDB="posttransfer"
#backup-mysqldb $BACKPOSTDB
# uncomment to restore posttransfer vivo database
#restore-mysqldb $BACKPOSTDB

#Restart Tomcat
#Tomcat must be restarted in order for the harvested data to appear in VIVO
#echo $HARVESTER_TASK ' completed successfully'

#IMPORTANT: This line must exist AS-IS in every File Harvest script.  The server checks the output and uses this line to verify that the harvest completed.
echo 'File Harvest completed successfully' 


#rm -f "authenticate?loginName=${ADMINNAME}&loginPassword=${ADMINPASS}&loginForm=1"
#rm -f cookie.txt
#wget --cookies=on --keep-session-cookies --save-cookies=cookie.txt "http://localhost:8080/vivo/authenticate?loginName=${ADMINNAME}&loginPassword=${ADMINPASS}&loginForm=1"

#rm -f "SearchIndex"
#wget --referer=http://first_page --cookies=on --load-cookies=cookie.txt --keep-session-cookies --save-cookies=cookie.txt http://localhost:8080/vivo/SearchIndex

#/etc/init.d/tomcat6 stop
#/etc/init.d/apache2 restart
#/etc/init.d/tomcat6 start
