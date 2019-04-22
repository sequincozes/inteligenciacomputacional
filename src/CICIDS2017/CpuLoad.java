/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CICIDS2017;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 *
 * @author sequi
 */
public class CpuLoad {

    static boolean ativar = false;

    public static void main(String[] args) {
        System.out.println(getProccessMemory());
    }

    public static double getProcessCpuLoad() throws Exception {
        if (ativar) {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
            AttributeList list = mbs.getAttributes(name, new String[]{"ProcessCpuLoad"});

            if (list.isEmpty()) {
                return Double.NaN;
            }

            Attribute att = (Attribute) list.get(0);
            Double value = (Double) att.getValue();

            // usually takes a couple of seconds before we get real values
            if (value == -1.0) {
                return Double.NaN;
            }
            // returns a percentage value with 1 decimal point precision
            return ((int) (value * 1000) / 10.0);
        } else {
            return 10000;
        }
    }

    public static double getProccessMemory() {
        String uso = "100000";
        if (ativar) {
            try {
                String line;
                Process p = Runtime.getRuntime().exec("tasklist");
                BufferedReader input
                        = new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((line = input.readLine()) != null) {
                    if (line.startsWith("java")) {
                        uso = line.substring(67).replace(" ", "").replace(".", "").replace("K", "");
                    }
                }
                input.close();
            } catch (Exception err) {
                err.printStackTrace();
            }
        }

        return Float.valueOf(uso);
    }
}
