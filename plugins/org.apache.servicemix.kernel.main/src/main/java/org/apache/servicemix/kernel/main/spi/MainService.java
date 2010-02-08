package org.apache.servicemix.kernel.main.spi;

public interface MainService {

    public java.lang.String[] getArgs();

    public int getExitCode();

    public void setExitCode(int arg0);
}
