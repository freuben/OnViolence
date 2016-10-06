NodePT {
	var <>window, <>time, task, <mag, <freq, <>number, <>audioIn=1;
	var myfreq, <>magnitudes, <>freqs, <>magThresh=0, <>m;
	var <a, <b, <c, <f, <j, s, <>y, <>o, <>t, <>z;
	
	*new {arg window=256, number=20;
		^super.new.initNodePT2(window, number);
	}
	
	initNodePT2 {arg windowSize=256, numberPartials=20;
		s = Server.local;
		window = windowSize;
		number = numberPartials;
		time = 2048/44100;
		
		a = Buffer.alloc(s, windowSize*2, 1, 1);
		b = Buffer.alloc(s, windowSize, 1, 1);
		c = Buffer.alloc(s, windowSize, 1, 1);
		
		y = NodeProxy.audio(s, 1);
		y.source = {arg bus=1, vol=1, maxDelay=30, delayTime=0.0; 
			DelayL.ar(AudioIn.ar(bus, vol), maxDelay, delayTime);}
		
		}

	
	clearNode {
	y.clear;
	}
	
	startsynth {arg witchIn=1;
		audioIn = witchIn;
		Routine({1.do({
		y.set(\bus, audioIn);
		y[1] = \filter -> { arg in, num=1; 
		var chain;
		chain = FFT(a.bufnum, in);
		chain = PV_MaxMagN(chain, num);
		chain = PV_MagBuffer(chain, b.bufnum);
		chain = PV_FreqBuffer(chain, c.bufnum);
		IFFT(chain);
		};
		0.1.yield;
		y.objects[1].set(\num, number+1);
		})}).play;
	}
	
	startsynth2 {arg witchIn=1, thresh = 0;
		audioIn = witchIn;
		magThresh = thresh;
		Routine({1.do({
		y.set(\bus, audioIn);
		y[1] = \filter -> { arg in, thr=0.1, num=1; 
		var chain;
		chain = FFT(a.bufnum, in);
		chain = PV_MagAbove(chain, thr);
		chain = PV_MaxMagN(chain, num);
		chain = PV_MagBuffer(chain, b.bufnum);
		chain = PV_FreqBuffer(chain, c.bufnum);
		IFFT(chain);
		};
		0.1.yield;
		y.objects[1].set(*[\thr, magThresh, \num, number+1]);
		})}).play;
	}
	
	numpartials {arg numberPartials = 20;
		number = numberPartials;
		y.objects[1].set(\num, number+1);
	}
	
	thresh {arg thresh = 0;
		magThresh = thresh;
		 y.objects[1].set(\thr, magThresh);
	}
		
	getarrays {
		window = window-1;
		Routine({1.do({myfreq = [];
			mag = [];
	
				
			  b.getn(0,window,{|msg| f = msg}); //get mag info from buffer
			  	c.getn(0,window,{|msg| j = msg}); //get freq info from buffer
			  	if(f.do{|item, index| 
			  			if(item != 0,
				  			 {mag = mag.add(item); myfreq = myfreq.add(index)}
				  		) //select max magnitudes and their index
				  	}.notNil, 
					{freq = j[myfreq]; //get freqs for selected magnitudes
					},
					{''} //if mag is nil, ignore
				);	
					
			time.yield;
	
			task = Task({
			inf.do({
			myfreq = [];
			magnitudes = [];
			
				b.getn(0,window,{|msg| f = msg});
				c.getn(0,window,{|msg| j = msg}); 
				f.do{|item, index| if(item != 0, {
					magnitudes = magnitudes.add(item); 
					myfreq = myfreq.add(index)
					})};
				  	
					freqs = j[myfreq];
					freqs = freqs.copyRange(1,freqs.size);
					magnitudes = magnitudes.copyRange(1, magnitudes.size);
					time.yield;
					})
				}).play
			})
			 
		}).play
	}
	
	stoptask {task.stop;}
	
	resume {task.resume;}
	
	list {[freqs,magnitudes].flop.dopostln}
	
	setBus {arg newBus=1;
	audioIn = newBus;
	y.set(\bus, audioIn);
	}
	
	micVolume {arg vol=1;
	y.set(\vol, vol);
	}
	
	newIn {arg func={SinOsc.ar};
	y[0] = func;
	}
	
	orgIn {arg witchIn=1;
	audioIn = witchIn;
	Routine({1.do({
	y[0] = {arg bus=1, vol=1, maxDelay=30, delayTime=0.0; 
		DelayL.ar(AudioIn.ar(bus, vol), maxDelay, delayTime);};
	0.05.yield;
	y.set(\bus, audioIn);
	})}).play;
	}
		
	free {
	//free synths and buffers and stops reading info
	y.free;
	this.stoptask;
	a.free;
	b.free;
	c.free;
	}
	
	setDelay {arg del=0;
	y.set(\delayTime, del);
	}
	
	frequencies {arg func={|item| item};
	^func.value(this.freqs);
	}
	
}


