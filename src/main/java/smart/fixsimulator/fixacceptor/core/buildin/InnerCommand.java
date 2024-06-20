/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Send email to lyziuu@gmail.com for any question.
 *
 */

package smart.fixsimulator.fixacceptor.core.buildin;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * inner command
 *
 * @author Leedeper
 */
@Slf4j
public class InnerCommand {

    private static HashMap<String,Command> mapping =new HashMap<>();
    static{
        mapping.put("RandomInt", new RandomInt());
        mapping.put("RandomFloat", new RandomFloat());
        mapping.put("Sequence", new Sequence());
        mapping.put("UTCDate", new UTCDate());
        mapping.put("UTCTime", new UTCTime());
        mapping.put("UTCDateTime", new UTCDateTime());
    }

    public static String getValue(String originalText){
        // Prevent someone from calling in the wrong time.
        if(originalText.startsWith("sp:")){
            log.error("The timing of the call is incorrect, don't support sp: command ");
            return null;
        }
        String nameAndParameter[] = originalText.split("\\(");
        if(nameAndParameter.length != 2){
            throw new RuntimeException("Invalid command. no ( or more - "+originalText);
        }
        String name = nameAndParameter[0];
        String theTail = nameAndParameter[1];
        if(theTail.charAt(theTail.length()-1)!=')'){
            throw new RuntimeException("Invalid command. no ) as end - "+originalText);
        }
        theTail = theTail.substring(0,theTail.length()-1);

        String allParameter[] = theTail.split(",");
        String ps[]= new String[allParameter.length];
        for(int i=0;i<allParameter.length;i++){
            ps[i]=allParameter[i].trim();
        }
        return getValue(name,allParameter);
    }

    private static String getValue(String name, String [] parameters){
        Command cmd = mapping.get(name);
        if(cmd!=null){
            return cmd.process(parameters);
        }
        return null;
    }
    private static class UTCDate implements Command{
        @Override
        public String process(String[] parameters) {
            return LocalDate.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
    }
    private static class UTCTime implements Command{
        @Override
        public String process(String[] parameters) {
            return LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
    }
    // format 20240618-02:23:06
    private static class UTCDateTime implements Command{

        @Override
        public String process(String[] parameters) {
            return LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss"));
        }
    }

    // sequence from file
    private static class Sequence implements Command {
        public String process(String[] parameters) {
            String seqFile;
            if(parameters.length==0){
                seqFile = "./simulator.seq";
            } else {
                seqFile = parameters[0];
            }
            try {
                return FileSequence.getSeq(seqFile).get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private static class RandomInt implements Command{
        public String process(String[] parameters){
            if(parameters.length==0){
                return String.valueOf(ThreadLocalRandom.current().nextLong());
            } else if(parameters.length==1){
                return String.valueOf(ThreadLocalRandom.current().nextLong(Long.parseLong(parameters[0])));
            } else if (parameters.length==2){
                long min = Long.parseLong(parameters[0]);
                long max = Long.parseLong(parameters[1]);
                return String.valueOf(ThreadLocalRandom.current().nextLong(min, max));
            } else {
                log.warn("Unsupported parameters {} in RandomInt Command, used 0", Arrays.toString(parameters));
                return "0";
            }
        }
    }

    private static class RandomFloat implements Command{
        public String process(String[] parameters){
            Double num;
            if(parameters.length==0){
                num = ThreadLocalRandom.current().nextDouble();
            }else if(parameters.length==1){
                num = ThreadLocalRandom.current().nextDouble(Double.parseDouble(parameters[1]));
            } else if (parameters.length==2){
                double min = Double.parseDouble(parameters[0]);
                double max = Double.parseDouble(parameters[1]);
                num = ThreadLocalRandom.current().nextDouble(min, max);
            } else {
                log.warn("Unsupported parameters {} in RandomFloat Command, used 0.00", Arrays.toString(parameters));
                num = 0D;
            }
            DecimalFormat df = new DecimalFormat("#.00");
            return df.format(num);
        }
    }

   private static class FileSequence{
        private static ConcurrentHashMap<String, FileSequence> allSeqs =new ConcurrentHashMap();
        private String fileName;
        public static synchronized FileSequence getSeq(String seqFile) throws IOException {
            Path path = Paths.get(seqFile);
            if(!Files.exists(path)){
                Files.createFile(path);
            }
            String fullPath = new File(seqFile).getAbsolutePath();
            FileSequence fseq = allSeqs.get(fullPath);
            if(fseq==null){
                fseq = new FileSequence(fullPath);
                allSeqs.put(fullPath,new FileSequence(fullPath));
            }
            return fseq;
        }
        public FileSequence(String fileName){
            this.fileName = fileName;
        }

        private synchronized String get() throws IOException {
            Path path = Paths.get(fileName);
            String curr = new String(Files.readAllBytes(path)).trim();
            if(curr.isEmpty()){
                curr="1";
            }
            String value = String.format("%d",Long.parseLong(curr)+1);
            Files.write(path,value.getBytes());
            return value;

        }

    }

    private static interface Command{
        public String process(String[] parameters);
    }
}
