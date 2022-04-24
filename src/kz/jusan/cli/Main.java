package kz.jusan.cli;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        help();
        System.out.print("> ");
        String command;
        String path="."; // default current directory
        String line = input.nextLine();
        String permission="";
        String[] query;



            try {
                while(!line.equals("exit")) {
                    query = line.split(" ");
                    command = query[0];
                    if (query.length > 1) {
                        path = query[1];
                    }
                    if(query.length>2) {
                        permission = query[2];
                    }
                    //Scanner sc = new Scanner(file);
                    switch (command) {
                        case "exit":
                            exit(); break;
                        case "help":
                            help(); break;
                        case "ls":
                            listDirectory(path); break;
                        case "ls_py":
                            listPythonFiles(path); break;
                        case "is_dir":
                            isDirectory(path); break;
                        case "define":
                            define(path); break;
                        case "readmod":
                            printPermissions(path); break;
                        case "setmod":
                            setPermissions(path, permission);
                        case "cat":
                            printContent(path); break;
                        case "append":
                            appendFooter(path); break;
                        case "bc":
                            createBackup(path); break;
                        case "greplong":
                            printLongestWord(path); break;
                        default:
                            System.out.println("No valid command found. Type help to view commands.");
                    }
                    System.out.print("> ");
                    line=input.nextLine();
                }
            }
            catch(Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }


    }




    // выводит список всех файлов и директорий для `path` - ls
    public static void listDirectory(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        for (File f : files) {
            System.out.print(f.getName() + " ");
        }
        System.out.println();
    };
    // выводит список файлов с расширением `.py` в `path` - ls_py
    public static void listPythonFiles(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        for (File f: files) {
            String extension = "";

            int i = f.getName().lastIndexOf('.');
            if (i > 0) {
                extension = f.getName().substring(i+1);
            }
            if(extension.equals("py")) {
                System.out.print(f.getName()+ " ");
            }
        }
        System.out.println();
    };
    // выводит `true`, если `path` это директория, в других случаях `false` - id_dir
    public static void isDirectory(String path) {
        File file = new File(path);
        if(file.isDirectory()) {
            System.out.println(true);
        } else {
            System.out.println(false);
        }
    }
    // выводит `директория` или `файл` в зависимости от типа `path` - define
    public static void define(String path) {
        File file = new File(path);
        if(file.isDirectory()) {
            System.out.println("директория");
        } else {
            System.out.println("файл");
        }
    }
    // выводит права для файла в формате `rwx` для текущего пользователя - readmod
    public static void printPermissions(String path) {
        StringBuilder sb = new StringBuilder("rwx");
        File file = new File(path);
        if(file.exists() && file.isFile()) {
            if(!file.canRead())
                sb.setCharAt(0, '-');
            if(!file.canWrite())
                sb.setCharAt(1, '-');
            if(!file.canExecute())
                sb.setCharAt(2, '-');
            System.out.println("File permission for " + file.getName() + ": " + sb.toString());
        }

    }
    // устанавливает права для файла `path` - setmod
    public static void setPermissions(String path, String permissions) {
        File file = new File(path);

        if(permissions.charAt(0) == 'r')
            file.setReadable(true);
        else
            file.setReadable(false);

        if(permissions.charAt(1) == 'w')
            file.setWritable(true);
        else
            file.setWritable(false);

        if(permissions.charAt(2) == 'x')
            file.setExecutable(true);
        else
            file.setExecutable(false);

    }
    // выводит контент файла - cat
    public static void printContent(String path) {
        File file = new File(path);
        String text = "";
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch(IOException e) {
            e.printStackTrace();
        }


        while(sc.hasNextLine()) {
            text += sc.nextLine()+"\n";
        }
        System.out.println(text);
    }
    // добавляет строке `# Autogenerated line` в конец `path` - append
    public static void appendFooter(String path) {
        try {
            FileWriter fw = new FileWriter(path, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("# Autogenerated line");
            bw.newLine();
            bw.close();
        } catch(IOException e) {
            System.out.println(e);
        }
    }
    // создает копию `path` в директорию `/tmp/${date}.backup` где, date - это дата в формате `dd-mm-yyyy`. `path` может быть директорией или файлом. При директории, копируется весь контент. - bc
    public static void createBackup(String path) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String today = dtf.format(LocalDateTime.now()).toString();
        String destinationString = "/tmp/${" + today + "}.backup/";

        File destinationFolder = new File(destinationString);
        if(!destinationFolder.exists()) {
            destinationFolder.mkdir();
        }

        File file = new File(path);
        //if(file.isFile()) {
            destinationString += file.getName();
        //}

        Path source = Paths.get(path);
        Path destination = Paths.get(destinationString);

        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
    }
    // выводит самое длинное слово в файле - greplong
    public static void printLongestWord(String path) {
        File file = new File(path);
        String longestWord="";
        int longestWordLength=0;
        String line;
        String[] words;
        try {
            Scanner sc = new Scanner(file);
            while(sc.hasNextLine()) {
                line = sc.nextLine();
                words = line.split(" ");
                for(String word: words) {
                    if(longestWordLength < word.length()) {
                        longestWordLength = word.length();
                        longestWord = word;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("The longest word in " + file.getName() + " is " + longestWord);
    }
    // выводит список команд и их описание - help
    public static void help() {
        System.out.println("MyFS 1.0 команды:\n" +
                "ls <path>               выводит список всех файлов и директорий для `path`\n" +
                "ls_py <path>            выводит список файлов с расширением `.py` в `path`\n" +
                "is_dir <path>           выводит `true`, если `path` это директория, в других случаях `false`\n" +
                "define <path>           выводит `директория` или `файл` в зависимости от типа `path`\n" +
                "readmod <path>          выводит права для файла в формате `rwx` для текущего пользователя\n" +
                "setmod <path> <perm>    устанавливает права для файла `path`\n" +
                "cat <path>              выводит контент файла\n" +
                "append <path>           добавляет строку `# Autogenerated line` в конец `path`\n" +
                "bc <path>               создает копию `path` в директорию `/tmp/${date}.backup` где, date - это дата в формате `dd-mm-yyyy`\n" +
                "greplong <path>         выводит самое длинное слово в файле\n" +
                "help                    выводит список команд и их описание\n" +
                "exit                    завершает работу программы");
    }
    // завершает работу программы - exit
    public static void exit() {
        System.out.println("Goodbye!");
        return;
    }
}
