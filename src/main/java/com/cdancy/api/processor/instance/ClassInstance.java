/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cdancy.api.processor.instance;

import com.cdancy.api.processor.annotations.ErrorHandler;
import com.cdancy.api.processor.annotations.ExecutionHandler;
import com.cdancy.api.processor.annotations.FallbackHandler;
import com.cdancy.api.processor.annotations.ResponseHandler;
import com.cdancy.api.processor.handlers.AbstractErrorHandler;
import com.cdancy.api.processor.handlers.AbstractExecutionHandler;
import com.cdancy.api.processor.handlers.AbstractFallbackHandler;
import com.cdancy.api.processor.handlers.AbstractResponseHandler;
import com.cdancy.api.processor.handlers.ProcessorHandles;
import com.cdancy.api.processor.cache.ProcessorCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.reflect.Invokable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

/**
 *
 * @author cdancy
 */
public class ClassInstance implements ProcessorHandles {
    
    private final Class clazz;
    private final ImmutableMap<String, Annotation> annotations;
    private final Map<String, MethodInstance> methodInstanceCache = Maps.newConcurrentMap();

    private final Class<? extends AbstractExecutionHandler> executionHandler;
    private final Class<? extends AbstractErrorHandler> errorHandler;
    private final Class<? extends AbstractFallbackHandler> fallbackHandler;
    private final Class<? extends AbstractResponseHandler> responseHandler;

    public ClassInstance(Class clazz) {
        this.clazz = clazz;
        
        Class localExecutionHandler = null;
        Class localErrorHandler = null;
        Class localFallbackHandler = null;
        Class localResponseHandler = null;
        
        ImmutableMap.Builder<String, Annotation> mapBuilder = ImmutableMap.builder();
        for (Annotation clazzAnnotation : clazz.getAnnotations()) {
            mapBuilder.put(clazzAnnotation.annotationType().getName(), clazzAnnotation); 
        }
        this.annotations = mapBuilder.build();
        
        Annotation possibleAnnotation = this.annotations.get(ExecutionHandler.class.getName());
        if (possibleAnnotation != null) {
            ExecutionHandler anno = (ExecutionHandler)possibleAnnotation;
            localExecutionHandler = anno.value();
        }
        possibleAnnotation = this.annotations.get(ErrorHandler.class.getName());
        if (possibleAnnotation != null) {
            ErrorHandler anno = (ErrorHandler)possibleAnnotation;
            localErrorHandler = anno.value();
        }
        possibleAnnotation = this.annotations.get(FallbackHandler.class.getName());
        if (possibleAnnotation != null) {
            FallbackHandler anno = (FallbackHandler)possibleAnnotation;
            localFallbackHandler = anno.value();
        }
        possibleAnnotation = this.annotations.get(ResponseHandler.class.getName());
        if (possibleAnnotation != null) {
            ResponseHandler anno = (ResponseHandler)possibleAnnotation;
            localResponseHandler = anno.value();
        }
        
        this.executionHandler = localExecutionHandler;
        this.errorHandler = localErrorHandler;
        this.fallbackHandler = localFallbackHandler;
        this.responseHandler = localResponseHandler;
    }
    
    public Class clazz() {
        return clazz;
    }
    
    public ImmutableMap<String, Annotation> annotations() {
        return annotations;
    }
    
    public <T> T getAnnotation(Class<T> clazz) {
        Annotation anno = annotations.get(clazz.getName());
        return (anno != null) ? clazz.cast(anno) : null;
    }
    
    public MethodInstance get(Class clazz, Method method) {
        MethodInstance methodInstance = methodInstanceCache.get(method.getName());
        if (methodInstance == null) {
            Invokable invokable = ProcessorCache.invokableFrom(clazz, method);            
            final MethodInstance newMethodInstance = new MethodInstance(invokable.getName(), invokable.getAnnotations(), invokable.getParameters(), invokable.getReturnType());
            methodInstanceCache.put(method.getName(), newMethodInstance);
            methodInstance = newMethodInstance;
        } 
        return methodInstance;
    }

    @Override
    public Class<? extends AbstractExecutionHandler> executionHandler() {
        return this.executionHandler;
    }

    @Override
    public Class<? extends AbstractErrorHandler> errorHandler() {
        return this.errorHandler;
    }

    @Override
    public Class<? extends AbstractFallbackHandler> fallbackHandler() {
        return this.fallbackHandler;
    }

    @Override
    public Class<? extends AbstractResponseHandler> responseHandler() {
        return this.responseHandler;
    }
}