package org.sader.security.service;


import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONObject;
import org.sader.security.model.Port;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RestService {
    final String logFile = "log.txt";
    final String passwordEchoText = "echo ghghjvdsh | ";
    boolean iranAccess = false;

    @PostConstruct
    public void ban(){
        try {
            Scanner scanner = new Scanner(new File("/home/mostafa/MyOwn/university/security/src/main/resources/static/iran_ip.txt"));
            runCommand("ipset create iranA nethash");
            while (scanner.hasNextLine()){
                String ip = scanner.nextLine();
                runCommand("ipset add iranA " + ip);
            }
            System.out.println("done");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public boolean openSSHForIp(String ip) {
        String command = String.format(passwordEchoText + " ufw allow from %s to any port 22", ip);
        System.out.println(command);
        try {
            runCommand(command);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public void log(HttpServletRequest request) {
        try {
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(logFile, true));
            StringBuilder log = new StringBuilder();
            log.append(LocalDateTime.now().toString());
            log.append(" ");
            log.append(request.getRemoteAddr());
            log.append(" ");
            log.append(request.getHeader("User-Agent"));
            log.append("\n");
            writer.write(log.toString());
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkURL(String url) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            return result.getStatusCode() == HttpStatus.valueOf(200);
        } catch (Exception e) {
            return false;
        }
    }

    public Set<Port> checkPorts(String ip) {
        try {

            Set<Port> ports = new HashSet<>();
            Port PORT_80 = new Port(80, isPortOpen(ip, 80));
            Port PORT_443 = new Port(443,  isPortOpen(ip, 443));
            Port PORT_22 = new Port(22,  isPortOpen(ip, 22));
            Port PORT_25 = new Port(25,  isPortOpen(ip, 25));
            ports.add(PORT_25);
            ports.add(PORT_22);
            ports.add(PORT_443);
            ports.add(PORT_80);
            return ports;
        } catch (
                CommandException e) {
            return null;
        }
    }

    private Port.PortStatus isPortOpen(String ip, int portNumber) throws CommandException {
        try {
            String txt = runCommand("nmap -p " + portNumber + " " + ip);
            Scanner scanner = new Scanner(txt);
            for (int i = 0; i < 5; i++) {
                scanner.nextLine();
            }
            String line = scanner.nextLine();
            List<String> strings = Arrays.stream(line.split(" ")).filter(str -> !str.equals("")).collect(Collectors.toList());
            return strings.get(1).equals("open") ? Port.PortStatus.OPEN : Port.PortStatus.CLOSE;
        } catch (CommandException e) {
            throw e;
        } catch (Exception e) {
            return Port.PortStatus.CLOSE;
        }
    }

    private String runCommand(String command) throws CommandException {
        command = passwordEchoText + " sudo -S " + command;
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", command);
        try {
            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
            int exitVal = process.waitFor();
            if (exitVal == 0) {
                return output.toString();
            } else {
                throw new CommandException();
            }
        } catch (Exception e) {
            throw new CommandException();
        }
    }

    public boolean isAccessible(String ip) {
        try {
            String command =  "curl ipinfo.io/" + ip;
            JSONObject json = new JSONObject(runCommand(command));
            return json.getString("country").equals("IR");
        }catch (Exception e){
            return false;
        }
    }

    public boolean toggleIranAccess() {
        try {
            iranAccess = !iranAccess;
            if (iranAccess) {
                runCommand("iptables -A INPUT -m set --match-set iranA src -j ACCEPT");
                runCommand("iptables -P INPUT DROP");
            }else {
                runCommand("iptables -P INPUT ACCEPT");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return iranAccess;

    }

    private static class CommandException extends Exception {

    }
}
