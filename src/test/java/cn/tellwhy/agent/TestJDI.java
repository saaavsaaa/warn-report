package cn.tellwhy.agent;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
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
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.tools.jdi.SocketAttachingConnector;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static sun.management.jmxremote.ConnectorBootstrap.PropertyNames.HOST;
import static sun.management.snmp.AdaptorBootstrap.PropertyNames.PORT;

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
        arg.setValue("cn.tellwhy.DailyTest");
        
        VirtualMachine targetVM = defaultConnector.launch(arguments);

        // 获取 Mirror 对象实例的虚拟机
        VirtualMachine virtualMachine = targetVM.virtualMachine();
        virtualMachine.allClasses().forEach(a -> System.out.println("===============:" + a.name()));
    }
    
    static VirtualMachine vm;
    static EventQueue eventQueue;
    static EventSet eventSet;
    static boolean vmExit = false;
    @Test
    public void rTest() throws Exception {
        VirtualMachineManager vmm = Bootstrap.virtualMachineManager();
        List<AttachingConnector> connectors = vmm.attachingConnectors();
        SocketAttachingConnector sac = null;
        for (AttachingConnector ac : connectors) {
            if (ac instanceof SocketAttachingConnector) {
                sac = (SocketAttachingConnector) ac;
                break;
            }
        }
        if (sac == null) {
            System.out.println("JDI error");
            return;
        }
        Map arguments = sac.defaultArguments();
        Connector.Argument hostArg = (Connector.Argument) arguments.get(HOST);
        Connector.Argument portArg = (Connector.Argument) arguments.get(PORT);
    
        hostArg.setValue("127.0.0.1");
        portArg.setValue(String.valueOf(9999));
    
        vm = sac.attach(arguments);
    
        List<ReferenceType> classesByName = vm.classesByName("cn.tellwhy.DailyTest");
        if (classesByName == null || classesByName.size() == 0) {
            System.out.println("No class found");
            return;
        }
        ReferenceType rt = classesByName.get(0);
        List<Method> methodsByName = rt.methodsByName("daily");
        if (methodsByName == null || methodsByName.size() == 0) {
            System.out.println("No method found");
            return;
        }
        Method method = methodsByName.get(0);
    
        vm.setDebugTraceMode(VirtualMachine.TRACE_EVENTS);
        vm.resume();
        EventRequestManager erm = vm.eventRequestManager();
    
        MethodEntryRequest methodEntryRequest = erm.createMethodEntryRequest();
        methodEntryRequest.addClassFilter(rt);
        methodEntryRequest.setSuspendPolicy(EventRequest.SUSPEND_NONE);
        methodEntryRequest.enable();
    
        BreakpointRequest breakpointRequest = erm
                .createBreakpointRequest(method.location());
        breakpointRequest.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
        breakpointRequest.enable();
    
        eventLoop();
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
                Event event = (Event) eventIterator.next();
                execute(event);
            }
        }
    }
    
    private static void execute(Event event) throws Exception {
        if (event instanceof VMStartEvent) {
            System.out.println("VM started");
            eventSet.resume();
        } else if (event instanceof BreakpointEvent) {
            System.out
                    .println("Reach Method printHello of test.Test");
            eventSet.resume();
        } else if (event instanceof MethodEntryEvent) {
            MethodEntryEvent mee = (MethodEntryEvent) event;
            Method method = mee.method();
            System.out.println(method.name() + " was Entered!");
            eventSet.resume();
        } else if (event instanceof VMDisconnectEvent) {
            vmExit = true;
        } else {
            eventSet.resume();
        }
    }
}

class debugger {
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
        mainArg.setValue("cn.tellwhy.DailyTest");
        suspendArg.setValue("true");
        vm = launchingConnector.launch(defaultArguments);
        
        process = vm.process();
        
        // Register ClassPrepareRequest
        eventRequestManager = vm.eventRequestManager();
        ClassPrepareRequest classPrepareRequest = eventRequestManager.createClassPrepareRequest();
//        classPrepareRequest.addClassFilter("cn.tellwhy.DailyTest");
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
            if (mainClassName.equals("cn.tellwhy.DailyTest")) {
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
