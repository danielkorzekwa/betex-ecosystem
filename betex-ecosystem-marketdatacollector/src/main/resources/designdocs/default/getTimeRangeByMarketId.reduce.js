function(keys,values,rereduce) {

var min=null;
var max=null;
for (i=0; i<values.length; ++i) {
    min = Math.min(values[i], min || values[i]);
    max = Math.max(values[i], max || values[i]);
}
return [min,max]

}