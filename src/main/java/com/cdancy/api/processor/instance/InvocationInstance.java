/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cdancy.api.processor.instance;

import com.cdancy.api.processor.handlers.AbstractErrorHandler;
import com.cdancy.api.processor.handlers.AbstractExecutionHandler;
import com.cdancy.api.processor.handlers.AbstractFallbackHandler;
import com.cdancy.api.processor.handlers.AbstractResponseHandler;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import java.lang.annotation.Annotation;
import javax.annotation.Nullable;

/**
 *
 * @author cdancy.
 * @param <T>
 */
public class InvocationInstance<T> {
    
    private final Class<T> clazz;
    private final ImmutableMap<String, ImmutableList<Annotation>> classAnnotations;
    private final String method;
    private final ImmutableMap<String, Annotation> methodAnnotations;
    private final ImmutableList<ParameterInstance<?>> parameterInstanceCache;
    private final Object [] arguments;
    private final TypeToken returnType;
    
    @Nullable
    private final AbstractExecutionHandler executionHandler;
    
    @Nullable
    private final AbstractErrorHandler errorHandler;
        
    @Nullable
    private final AbstractFallbackHandler fallbackHandler;

    @Nullable
    private final AbstractResponseHandler responseHandler;
        
    private InvocationInstance(Class<T> clazz, 
            ImmutableMap<String, ImmutableList<Annotation>> classAnnotations, 
            String method, 
            ImmutableMap<String, Annotation> methodAnnotations, 
            ImmutableList<ParameterInstance<?>> parameterInstanceCache,
            Object [] arguments,
            TypeToken returnType,
            AbstractExecutionHandler executionHandler,
            AbstractErrorHandler errorHandler,
            AbstractFallbackHandler fallbackHandler,
            AbstractResponseHandler responseHandler) {
        this.clazz = clazz;
        this.classAnnotations = classAnnotations;
        this.method = method;
        this.methodAnnotations = methodAnnotations;
        this.parameterInstanceCache = parameterInstanceCache;
        this.arguments = arguments;
        this.returnType = returnType;
        this.executionHandler = executionHandler;
        this.errorHandler = errorHandler;
        this.fallbackHandler = fallbackHandler;
        this.responseHandler = responseHandler;
    }
    
    public Class<T> clazz() {
        return clazz;
    }
    
    public ImmutableMap<String, ImmutableList<Annotation>> classAnnotations() {
        return classAnnotations;
    } 
    
    public <T> T firstClassAnnotation(Class<T> clazz) {
        ImmutableList<Annotation> annos = classAnnotations().get(clazz.getName());
        if (annos != null) {
            return clazz.cast(annos.get(0));
        } else {
            return null;
        }
    }
    
    public <T> T lastClassAnnotation(Class<T> clazz) {
        ImmutableList<Annotation> annos = classAnnotations().get(clazz.getName());
        if (annos != null) {
            return clazz.cast(annos.get(annos.size() - 1));
        } else {
            return null;
        }
    }
    
    public ImmutableList<Annotation> classAnnotations(Class clazz) {
        return classAnnotations().get(clazz.getName());
    }
    
    public String method() {
        return method;
    }
    
    public ImmutableMap<String, Annotation> methodAnnotations() {
        return methodAnnotations;
    } 
    
    public <T> T methodAnnotation(Class<T> clazz) {
        Annotation anno = methodAnnotations().get(clazz.getName());
        return (anno != null) ? clazz.cast(anno) : null;
    }
    
    public ParameterInstance parameterInstance(int index) {
        ParameterInstance parameterInstance = parameterInstanceCache.get(index);
        parameterInstance.setValue(arguments[index]);
        return parameterInstance;
    }
    
    public int parameterCount() {
        return arguments.length;
    }
    
    public TypeToken returnType() {
        return returnType;
    }
    
    @Override
    public String toString() {
        return (this.clazz().getName() + "@" + this.method()).intern();
    }
    
    /**
     * Create new InvocationInstance from passed parameters.
     * 
     * @param classInstance the classInstance used to query for annotations.
     * @param methodInstance the methodInstance used to query for annotations.
     * @param args the parameter arguments for this invocation.
     * @param executionHandler the executionHandler used for this invocation.
     * @param errorHandler the errorHandler used for this invocation.
     * @param fallbackHandler the fallbackHandler used for this invocation.
     * @param responseHandler the responseHandler used for this invocation.
     * @return newly created InvocationInstance.
     */
    public static InvocationInstance newInstance(ClassInstance classInstance, 
            MethodInstance methodInstance, 
            Object [] args,
            AbstractExecutionHandler executionHandler,
            AbstractErrorHandler errorHandler,
            AbstractFallbackHandler fallbackHandler,
            AbstractResponseHandler responseHandler) {
        
        return new InvocationInstance(classInstance.clazz(), 
                classInstance.annotations(), 
                methodInstance.method(), 
                methodInstance.annotations(), 
                methodInstance.parameterInstanceCache(),
                args,
                methodInstance.returnType(), 
                executionHandler, 
                errorHandler, 
                fallbackHandler, 
                responseHandler);
    }

    public AbstractExecutionHandler executionHandler() {
        return this.executionHandler;
    }

    public AbstractErrorHandler errorHandler() {
        return this.errorHandler;
    }

    public AbstractFallbackHandler fallbackHandler() {
        return this.fallbackHandler;
    }

    public AbstractResponseHandler responseHandler() {
        return this.responseHandler;
    }
}
