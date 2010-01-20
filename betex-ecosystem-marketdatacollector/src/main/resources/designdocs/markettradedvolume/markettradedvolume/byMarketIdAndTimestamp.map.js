function(doc) 
{
	emit([doc.marketId,doc.timestamp],doc);
}