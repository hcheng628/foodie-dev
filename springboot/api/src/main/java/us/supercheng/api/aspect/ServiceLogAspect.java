package us.supercheng.api.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLogAspect {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Around("execution(* us.supercheng.service.impl ..*.*(..))")
    public Object transTimeLog(ProceedingJoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getName(),
                name = joinPoint.getSignature().getName();

        LOG.info("====== Execute {}.{} ======", className, name);
        long start = System.currentTimeMillis();
        Object ret = null;

        try {
            ret = joinPoint.proceed();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable.getMessage());
        }

        long end = System.currentTimeMillis();
        long res = end - start;

        if (res > 3000)
            LOG.error("====== ERROR - Execute {}.{} ======" + res, className, name);
        else if (res > 2500)
            LOG.warn("====== WARN - Execute {}.{} ======" + res, className, name);
        else
            LOG.info("====== OK - Execute {}.{} ======" + res, className, name);

        return ret;
    }
}