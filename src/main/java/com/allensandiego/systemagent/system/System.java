package com.allensandiego.systemagent.system;

public class System {

    Processor processor;
    Memory memory;
    
    public System() {

    }

    public Processor getProcessor() {
        return processor;
    }

    public void setProcessor(Processor processor) {
        this.processor = processor;
    }    
    
    public Memory getMemory() {
        return memory;
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }

}
