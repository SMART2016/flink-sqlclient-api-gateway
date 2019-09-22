package org.apache.flink.sqlclient.api.controller;

import org.apache.flink.sqlclient.api.controller.executor.Executor;
import org.apache.flink.sqlclient.api.controller.executor.LocalExecutorImpl;
import org.apache.flink.sqlclient.api.controller.response.CustomResponseBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.flink.sqlclient.api.controller.util.UnzipUtility;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/streams/api/v1")
public class SqlClientGateway {

    /**
     * Get all users list.
     *
     * @return the list
     */
    @GetMapping("/client")
    public String getGreeting(@RequestParam(name = "action") String action) {
        System.out.println("Active window dipanjan");
        if (action.equals("isActive")) {
            return "I am active Friend";
        }
        return null;
    }

    /**
     * Api to upload a zip file containing environment yaml and jars for the current session.
     *
     * @param sid    session id for the current session
     * @param envZip zip containing environment yaml config file and the related jars
     * @return
     */
    @PostMapping("/session/{sid}")
    @ResponseBody
    public CustomResponseBody handleFileUpload(HttpServletResponse response, @PathVariable("sid") String sid, @RequestParam("file") MultipartFile envZip) {
        System.out.println("File for sessionid " + sid + "-->" + envZip.getOriginalFilename());

        //TODO: Add validation for zip file should be accepted only
        try {
            Map<String, InputStream> envFileIsMap = UnzipUtility.GetMapofEnvFiles(envZip.getInputStream(), UnzipUtility.GetValidEnvFileLstNames());
            if (envFileIsMap.size() == 0){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return new CustomResponseBody("No Valid Environment File Found","900");
            }
            final Executor executor = new LocalExecutorImpl(envFileIsMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new CustomResponseBody("Success","200");
    }

}

