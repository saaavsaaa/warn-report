package cn.tellwhy.agent;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by aaa
 */
public class TestJDI {
    @Test
    public void test() throws VMStartException, IllegalConnectorArgumentsException, IOException {
        // Debugger 通过 Bootstrap 获取唯一的虚拟机管理器
        VirtualMachineManager virtualMachineManager = Bootstrap.virtualMachineManager();
        // 虚拟机管理器将在第一次被调用时初始化可用的链接器
        LaunchingConnector defaultConnector = virtualMachineManager.defaultConnector();
        //  启动目标程序，连接调试器（Debugger）与目标虚拟机（VirtualMachine）
        Map<String, Connector.Argument> arguments = defaultConnector.defaultArguments();
        Connector.Argument arg = arguments.get("main");
        arg.setValue("DailyTest");
        
        VirtualMachine targetVM = defaultConnector.launch(arguments);

        // 获取 Mirror 对象实例的虚拟机
        VirtualMachine virtualMachine = targetVM.virtualMachine();
        virtualMachine.allClasses().forEach(a -> System.out.println("===============:" + a.name()));
    }
    
    static VirtualMachine vm;
    static Process process;
    static EventRequestManager eventRequestManager;
    static EventQueue eventQueue;
    static EventSet eventSet;
    static boolean vmExit = false;
    
    public static void main(String[] args) throws Exception{
        LaunchingConnector launchingConnector = Bootstrap.virtualMachineManager().defaultConnector();
        
        // Get arguments of the launching connector
        Map<String, Connector.Argument> defaultArguments = launchingConnector.defaultArguments();
        Connector.Argument mainArg = defaultArguments.get("main");
        Connector.Argument suspendArg = defaultArguments.get("suspend");
        // Set class of main method
        mainArg.setValue("DailyTest");
        suspendArg.setValue("true");
        vm = launchingConnector.launch(defaultArguments);
        
        process = vm.process();
        
        // Register ClassPrepareRequest
        eventRequestManager = vm.eventRequestManager();
        ClassPrepareRequest classPrepareRequest = eventRequestManager.createClassPrepareRequest();
//        classPrepareRequest.addClassFilter("DailyTest");
//        classPrepareRequest.addCountFilter(1);
//        classPrepareRequest.setSuspendPolicy(EventRequest.SUSPEND_ALL);
        classPrepareRequest.enable();
        
        // Enter event loop
        eventLoop();
        
        process.destroy();
    }
    
    private static void eventLoop() throws Exception {
        eventQueue = vm.eventQueue();
        while (true) {
            if (vmExit == true) {
                break;
            }
            eventSet = eventQueue.remove();
            EventIterator eventIterator = eventSet.eventIterator();
            while (eventIterator.hasNext()) {
                Event event = eventIterator.next();
                execute(event);
            }
        }
    }
    
    private static void execute(Event event) throws Exception {
        int line = 27;
        if (event instanceof VMStartEvent) {
            System.out.println("VM started");
            eventSet.resume();
        } else if (event instanceof ClassPrepareEvent) {
            ClassPrepareEvent classPrepareEvent = (ClassPrepareEvent) event;
            String mainClassName = classPrepareEvent.referenceType().name();
            System.out.println(mainClassName);
            if (mainClassName.equals("DailyTest")) {
                System.out.println("Class DailyTest is already prepared");
                // Get location
                ReferenceType referenceType = classPrepareEvent.referenceType();
                List locations = referenceType.locationsOfLine(line);
                Location location = (Location) locations.get(0);
    
                // Create BreakpointEvent
                BreakpointRequest breakpointRequest = eventRequestManager.createBreakpointRequest(location);
                breakpointRequest.setSuspendPolicy(EventRequest.SUSPEND_ALL);
                breakpointRequest.enable();
            }
            eventSet.resume();
        } else if (event instanceof BreakpointEvent) {
            System.out.println("Reach line " + line + " of DailyTest");
            BreakpointEvent breakpointEvent = (BreakpointEvent) event;
            ThreadReference threadReference = breakpointEvent.thread();
            StackFrame stackFrame = threadReference.frame(0);
            LocalVariable localVariable = stackFrame.visibleVariableByName("str");
            Value value = stackFrame.getValue(localVariable);
            String str = ((StringReference) value).value();
            System.out.println("The local variable str at line " + line + " is " + str + " of " + value.type().name());
            eventSet.resume();
        } else if (event instanceof VMDisconnectEvent) {
            vmExit = true;
        } else {
            eventSet.resume();
        }
    }
}
