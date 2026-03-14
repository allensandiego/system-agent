package com.allensandiego.systemagent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.KeyGenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.wavefront.WavefrontProperties.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.allensandiego.systemagent.system.Memory;
import com.allensandiego.systemagent.system.Processor;
import com.allensandiego.systemagent.system.System;

import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;
import oshi.SystemInfo;

@SpringBootApplication
@RestController
public class SystemAgentApplication {

	private System system = new System();
	private SystemInfo si = new SystemInfo();
	private HardwareAbstractionLayer hal = si.getHardware();
	private Sensors sensors = hal.getSensors();

	@GetMapping(value = "/system")
	public System system(@RequestParam(value = "memoryUnit", required = false) String memoryUnit) {

		Processor processor = new Processor();
		processor.setName(hal.getProcessor().getProcessorIdentifier().getName().trim());
		processor.setFamily(hal.getProcessor().getProcessorIdentifier().getFamily());
		processor.setModel(hal.getProcessor().getProcessorIdentifier().getModel());
		processor.setVendor(hal.getProcessor().getProcessorIdentifier().getVendor());
		processor.setCpuLoad(hal.getProcessor().getSystemCpuLoad(360) * 100);
		processor.setTemp(sensors.getCpuTemperature());

		Memory memory = new Memory();
		double available = hal.getMemory().getAvailable();
		double total = hal.getMemory().getTotal();
		double used = total - available;

		if (memoryUnit != null && memoryUnit.toLowerCase().equals("gb")) {
			available = available / (1024*1024*1024);
			total = total / (1024*1024*1024);
			used = total - available;
		}

		memory.setAvailable(available);
		memory.setTotal(total);
		memory.setUsed(used);

		system.setProcessor(processor);
		system.setMemory(memory);

		return system;
	}

	@PostMapping("/shutdown")
	public ResponseEntity<String> shutdown() {
		try {
			Runtime.getRuntime().exec(new String[] { "shutdown", "/p" });
			return new ResponseEntity<String>("", HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<String>("", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/reboot")
	public ResponseEntity<String> reboot() {
		try {
			Runtime.getRuntime().exec(new String[] { "shutdown", "/r" });
			return new ResponseEntity<String>("", HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<String>("", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private static Key getKeyFromKeyGenerator(String cipher, int keySize) throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance(cipher);
		keyGenerator.init(keySize);
		return keyGenerator.generateKey();
	}

	public static void initAuthorizationKey() {
		File authKeyFile = new File("authorizedKey.txt");

		try {
			if (authKeyFile.createNewFile()) {
				String key = Base64.getEncoder().encodeToString(getKeyFromKeyGenerator("AES", 256).getEncoded());
				FileWriter fWriter = new FileWriter(authKeyFile);
				fWriter.write(key);
				fWriter.flush();
				fWriter.close();
			}
		} catch (IOException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static ConfigurableApplicationContext context;
	public static void main(String[] args) {
		//SpringApplication.run(SystemAgentApplication.class, args);
		initAuthorizationKey();
		SpringApplicationBuilder builder = new SpringApplicationBuilder(SystemAgentApplication.class);
        builder.headless(false);
        context = builder.run(args);

        SystemAgentTrayIcon m = new SystemAgentTrayIcon();
	}

}
