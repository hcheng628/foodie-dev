package us.supercheng.services;

import org.springframework.web.multipart.MultipartFile;

public interface FastDFSService {
    String uploadFile(MultipartFile file, String extension) throws Exception;
}
