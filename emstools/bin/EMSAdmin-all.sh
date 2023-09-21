#!/bin/sh
###############################################################################
#
# Release 1_0_0:
# - First release.
#
# Release 1_0_1:
# - output folder is now ${DIRSCRIPT}/../emsadmin-all-<datetime>
#
# Release 1_3_5:
# - added queues and topics
#
# $Header: /cvs/TIL_SOURCE/scripts/env/MyDomain1/startAdmin.sh,v 1.5 2006/06/28 14:56:42 ayelr Exp $
###############################################################################

###############################################################################
# CHANGE THE FOLLOWING VARIABLE VALUES IF NECESSARY
###############################################################################

###############################################################################
# DO NOT CHANGE THE REST OF THE SCRIPT
###############################################################################

###############################################################################
# START

# folder of this script itself
DIRSCRIPT=`dirname "$0"`
DIRSCRIPT=`(cd "${DIRSCRIPT}" ; echo ${PWD})`

SCRIPT=`basename "$0"`

###############################################################################

# create folder for output data files
FOLDER=`date "+%Y%m%d-%H%M"`
FOLDER="${DIRSCRIPT}/../emsadmin-all-${FOLDER}"

mkdir -p "${FOLDER}"
FOLDER=`(cd "${FOLDER}" ; echo ${PWD})`

# invoke the EMSAdmin script
for i in acl bridges connectionFactories connections consumers durables factories fileStores groups producers queues routes topics users ; do
	echo "Writing file ${FOLDER}/${i}.csv..."
	${DIRSCRIPT}/EMSAdmin.sh $@ -print "${i}" > "${FOLDER}/${i}.csv"
done
echo "All CSV files are in this folder: ${FOLDER}"

###############################################################################
###  END OF FILE  #############################################################
###############################################################################



