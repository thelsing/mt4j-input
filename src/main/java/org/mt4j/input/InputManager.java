/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009, C.Ruff, Fraunhofer-Gesellschaft All rights reserved.
 *  
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ***********************************************************************/
package org.mt4j.input;

import java.util.*;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mt4j.input.inputProcessors.globalProcessors.AbstractGlobalInputProcessor;
import org.mt4j.input.inputSources.AbstractInputSource;
import org.mt4j.input.inputSources.IinputSourceListener;

import javax.swing.*;


/**
 * Manages the InputSources and Inputprocessors for each scene.
 * Starts up the default input sources.
 * 
 * @author Christopher Ruff
 */
public class InputManager {
	/** The Constant logger. */
	protected static final Logger logger = LogManager.getLogger(InputManager.class.getName());
	static{
//		logger.setLevel(ILogger.ERROR);
//		logger.setLevel(ILogger.DEBUG);
		logger.setLevel(Level.INFO);
	}
	
	/** The registered input sources. */
	private List<AbstractInputSource> registeredInputSources;
	
	/** The In processor to scene. */
	private Set<AbstractGlobalInputProcessor> inputProcessorsToScene;

	/** The pa. */
	protected JFrame app;
	
	
	/**
	 * Instantiates a new input manager.
	 * 
	 * @param pa the processing context
	 */
	public InputManager(JFrame pa) {
		this(pa, true);
	}
	
	
	/**
	 * Instantiates a new input manager.
	 * 
	 * @param pa the processing context
	 */
	public InputManager(JFrame pa, boolean registerDefaultSources) {
		super();
		this.registeredInputSources	= new ArrayList<AbstractInputSource>();
		this.inputProcessorsToScene = new HashSet<AbstractGlobalInputProcessor>();
		this.app = pa;
		
		if (registerDefaultSources)
			this.registerDefaultInputSources();
	}
	
	
	/**
	 * Initialize default input sources.
	 */
	protected void registerDefaultInputSources(){	}
	
	
	/**
	 * Registers a new input source for the application.
	 * 
	 * @param newInputSource the new input source
	 */
	public void registerInputSource(AbstractInputSource newInputSource){
		if (!registeredInputSources.contains(newInputSource)){
			registeredInputSources.add(newInputSource);
			//Add all processors to the new input source
            for (AbstractGlobalInputProcessor processor : inputProcessorsToScene) {
                //newInputSource.addInputListener(processor);
                this.saveAddInputListenerToSource(newInputSource, processor);
            }
			
			//Inform the input source that it is now registered with the application
			newInputSource.onRegistered();
		}else{
			logger.error("input source already registered! - " + newInputSource);
		}
	}
	
	
	/**
	 * Unregisters a input source.
	 * @param is the input source
	 */
	public void unregisterInputSource(AbstractInputSource is){
		synchronized (registeredInputSources) {
			if (registeredInputSources.contains(is)){
				registeredInputSources.remove(is);
				
				//Inform the input source that it is now UN-registered from the application
				is.onUnregistered();
			}
		}
	}
	
	/**
	 * Gets the input sources.
	 * @return the input sources
	 */
	public AbstractInputSource[] getInputSources(){
		return this.registeredInputSources.toArray(new AbstractInputSource[this.registeredInputSources.size()]);
	}
	

	
	/**
	 * Registers a new inputprocessor and adds it to the inputsources as listeners.
	 * 
	 * @param inputprocessor the input processor
	 */
	public void registerGlobalInputProcessor(AbstractGlobalInputProcessor inputprocessor){
		//By default disable the registered global input processor, so it doesent accidently
		//send events to a not even visible scene
		//-Only enable it if the scene is the currently active scene
		//-If a scene becomes active the processors will also be enabled
//		if (app.getCurrentScene() != null && app.getCurrentScene().equals(scene)){
	/*	if (scene.equals(app.getCurrentScene())){
			inputprocessor.setDisabled(false);
		}else{
			inputprocessor.setDisabled(true);
		}
	*/
		inputProcessorsToScene.add(inputprocessor);
		//Register the processor with all registered inputsources
		for (AbstractInputSource source: registeredInputSources){
			this.saveAddInputListenerToSource(source, inputprocessor);
		}
	}
	
	
	private void saveAddInputListenerToSource(AbstractInputSource source, AbstractGlobalInputProcessor inputprocessor){
		//Only add input processor to input sources 
		//that fire the event type that the processor is interested in
//		if (source.firesEventType(inputprocessor.getListenEventType())){
		
			List<IinputSourceListener> sourceListener = Arrays.asList(source.getInputListeners());
			if (!sourceListener.contains(inputprocessor)){ //Prevent adding same global input processor twice
				source.addInputListener(inputprocessor);
			}
//		}
	}
	
	/**
	 * Unregisters a inputprocessor from _all_ the registered inputsources.
	 * 
	 * @param inputprocessor the input processor
	 */
	public void unregisterGlobalInputProcessor(AbstractGlobalInputProcessor inputprocessor){
		/*
		Set set = InprocessorToScene.keySet(); 
		for (Iterator iter = set.iterator(); iter.hasNext();) {
			AbstractInputprocessor processor = (AbstractInputprocessor) iter.next();
			
			//Check if the processor is registered here with a scene
			if (processor.equals(inputprocessor)){
				if (InprocessorToScene.get(processor).equals(scene)){
					for (AbstractInputSource source: registeredInputSources){
						source.removeInputListener(inputprocessor);
					}
				}
			}
		}
		*/
		
		//Remove the input processor from the processor->scene map
		if (inputProcessorsToScene.contains(inputprocessor)){
			inputProcessorsToScene.remove(inputprocessor);	
		}
		
		for (AbstractInputSource source: registeredInputSources){
			source.removeInputListener(inputprocessor);
		}
	}
}
