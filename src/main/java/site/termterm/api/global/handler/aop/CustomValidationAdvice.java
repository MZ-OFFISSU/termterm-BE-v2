package site.termterm.api.global.handler.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import site.termterm.api.global.handler.exceptions.CustomValidationException;

import java.util.HashMap;
import java.util.Map;

@Component  // IoC 컨테이너 등록
@Aspect
public class CustomValidationAdvice {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMapping(){}

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void putMapping(){}


    /* @PostMapping 혹은 @PutMapping 이라는 어노테이션이 붙어있는 모든 컨트롤러가 실행이 될 때 동작,
     *  BindingResult 라는 매개변수가 있고, 유효성 검증 에러가 발생했을 때 CustomValidationException 을 throw 한다 */
    @Around("postMapping() || putMapping()")    // joinPoint의 전후 제어. Around만 joinPoint를 받을 수 있고, After/Before는 받을 수 없다
    public Object validationAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();  // joinPoint(Target Method) 들의 매개변수들을 읽어온다
        for (Object arg : args){
            if(arg instanceof BindingResult){
                BindingResult bindingResult = (BindingResult) arg;

                if(bindingResult.hasErrors()){
                    Map<String, String> errorMap = new HashMap<>();

                    for (FieldError error : bindingResult.getFieldErrors()){
                        errorMap.put(error.getField(), error.getDefaultMessage());
                    }

                    throw new CustomValidationException("유효성 검사 실패", errorMap);
                }
            }
        }

        return proceedingJoinPoint.proceed();   // 에러가 발생하지 않았다면, 정상적으로 해당 메서드를 실행하라
    }
}