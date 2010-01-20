function(keys,values,rereduce) {

var min=null;
var max=null;
for (i=0; i<values.length; ++i) {
    min = Math.min(values[i][0], min || values[i][0]);
    max = Math.max(values[i][1], max || values[i][1]);
}
return [min,max]

}