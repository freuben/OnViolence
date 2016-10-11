+ Collection {

  evenIndex {
    var newArray=[];
    this.do({|item, index|
      if(index.even, {
        newArray = newArray.add(item);
      });
    });
    ^newArray;
  }

  indexOfAll {arg list;
    var newArray=[];
    list.do({ |item| newArray = newArray.add(this.indexOf(item)); });
    ^newArray;
  }

  itemsAt {arg indexes;
    var newArray;
    newArray = [];
    indexes.do({|item| newArray = newArray.add(this.[item])});
    ^newArray;
  }

}