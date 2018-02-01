function test2abc(){
  console.log((+new Date));
  setTimeout(function(){
    //console.log(JSON.stringify());
    console.log("yyyyyyyyyyyy--test.js---"+JSON.stringify(LYUIHandle));
  },50);
 // console.log(JSON.stringify());
  LYUIHandle.testFun(function(s){
      console.log("---111111"+s);
  })
}