+ Server {

  bootAndMIDI {arg db=0;
    this.waitForBoot({
      MIDIIn.connectAll;
      this.volume = db;
    });
  }

}