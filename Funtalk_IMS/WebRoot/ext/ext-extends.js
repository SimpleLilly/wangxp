/**
* Ext组件扩展
*/

Ext.namespace('Ext.sitech');
/**
	className: Ext.sitech.SiComboBox
	xtype: sicombo
	功能描述：文本框自动完成,自定义sql语句搜索
	增加querySql参数,如: 
	'select fav_type "FAV_TYPE", note "NOTE" from (select fav_type 
	fav_type,max(note) note from fav_index group by fav_type )where 
	fav_type like \'${value}%\''
	配合 /opertable/SqlGenerateJsonData.jspa,生成json数据，包含"FAV_TYPE"，
	"NOTE"字段。
*/
/*Ext.form.SiComboBox = function(config){
	Ext.form.SiComboBox.superclass.constructor.call(this, config);
};*/
Ext.sitech.SiComboBox = Ext.extend(Ext.form.ComboBox, {
    
    querySql:'',

    initComponent : function(){
        Ext.sitech.SiComboBox.superclass.initComponent.call(this);
    },
    initList : function(){
        if(!this.list){
            var cls = 'x-combo-list';

            this.list = new Ext.Layer({
                shadow: this.shadow, cls: [cls, this.listClass].join(' '), constrain:false
            });

            var lw = this.listWidth || Math.max(this.wrap.getWidth(), this.minListWidth);
            this.list.setWidth(lw);
            this.list.swallowEvent('mousewheel');
            this.assetHeight = 0;

            if(this.title){
                this.header = this.list.createChild({cls:cls+'-hd', html: this.title});
                this.assetHeight += this.header.getHeight();
            }

            this.innerList = this.list.createChild({cls:cls+'-inner'});
            this.innerList.on('mouseover', this.onViewOver, this);
            this.innerList.on('mousemove', this.onViewMove, this);
            this.innerList.setWidth(lw - this.list.getFrameWidth('lr'));

            if(this.pageSize){
                this.footer = this.list.createChild({cls:cls+'-ft'});
                this.store.baseParams['querySql'] = this.querySql;
                this.pageTb = new Ext.PagingToolbar({
                    store:this.store,
                    pageSize: this.pageSize,
                    renderTo:this.footer
                });
                this.assetHeight += this.footer.getHeight();
            }

            if(!this.tpl){
			    
                this.tpl = '<tpl for="."><div class="'+cls+'-item">{' + this.displayField + '}</div></tpl>';
            }

		    
            this.view = new Ext.DataView({
                applyTo: this.innerList,
                tpl: this.tpl,
                singleSelect: true,
                selectedClass: this.selectedClass,
                itemSelector: this.itemSelector || '.' + cls + '-item'
            });

            this.view.on('click', this.onViewClick, this);

            this.bindStore(this.store, true);

            if(this.resizable){
                this.resizer = new Ext.Resizable(this.list,  {
                   pinned:true, handles:'se'
                });
                this.resizer.on('resize', function(r, w, h){
                    this.maxHeight = h-this.handleHeight-this.list.getFrameWidth('tb')-this.assetHeight;
                    this.listWidth = w;
                    this.innerList.setWidth(w - this.list.getFrameWidth('lr'));
                    this.restrictHeight();
                }, this);
                this[this.pageSize?'footer':'innerList'].setStyle('margin-bottom', this.handleHeight+'px');
            }
        }
    },
    doQuery : function(q, forceAll){
        if(q === undefined || q === null){
            q = '';
        }
        var qe = {
            query: q,
            forceAll: forceAll,
            combo: this,
            cancel:false
        };
        if(this.fireEvent('beforequery', qe)===false || qe.cancel){
            return false;
        }
        q = qe.query;
        forceAll = qe.forceAll;
        if(forceAll === true || (q.length >= this.minChars)){
            if(this.lastQuery !== q){
                this.lastQuery = q;
                if(this.mode == 'local'){
                    this.selectedIndex = -1;
                    if(forceAll){
                        this.store.clearFilter();
                    }else{
                        this.store.filter(this.displayField, q);
                    }
                    this.onLoad();
                }else{
                    this.store.baseParams[this.queryParam] = q;
                    this.store.load({
                        params: this.getParams(q)
                    });
                    this.expand();
                }
            }else{
                this.selectedIndex = -1;
                this.onLoad();
            }
        }
    },

        getParams : function(q){
        var p = {};
                if(this.pageSize){
            p.start = 0;
            p.limit = this.pageSize;
            p.querySql = this.querySql;
        }
        return p;
    }
});
Ext.reg('sicombo', Ext.sitech.SiComboBox);



Ext.apply(Ext.DataView.prototype, {
	deselect:function(node, suppressEvent){
    if(this.isSelected(node)){
			var node = this.getNode(node);
			this.selected.removeElement(node);
			if(this.last == node.viewIndex){
				this.last = false;
			}
			Ext.fly(node).removeClass(this.selectedClass);
			if(!suppressEvent){
				this.fireEvent('selectionchange', this, this.selected.elements);
			}
		}
	}
});

/**
	className: Ext.sitech.MultiSelect
	xtype: multiselect
	功能描述：多选下拉框
	参数　如: value:['m21b','m21e','j21m'],可默认选中下拉框中数据。
	配合 /opertable/GenerateJsonData.jspa?askType=55,生成json数据，包含局数据表id为55的所有字段。
 */
Ext.sitech.MultiSelect = function(config){
	if (config.transform && typeof config.multiSelect == 'undefined'){
		var o = Ext.getDom(config.transform);
		config.multiSelect = (Ext.isIE ? o.getAttributeNode('multiple').specified : o.hasAttribute('multiple'));
	}
	if (config.multiSelect){
		config.selectOnFocus = false;
	}
	config.hideTrigger2 = config.hideTrigger2||config.hideTrigger;
	Ext.sitech.MultiSelect.superclass.constructor.call(this, config);
}

Ext.extend(Ext.sitech.MultiSelect, Ext.form.ComboBox, {

	multiSelect:false,

	minLength:0,

	minLengthText:'Minimum {0} items required',

	maxLength:Number.MAX_VALUE,

	maxLengthText:'Maximum {0} items allowed',

	clearTrigger:true,

	history:false,

	historyMaxLength:0,
	//xuyd
	delimiter:',',
	
	editable:false,
	
	initComponent:function(){
		//from twintrigger
		this.triggerConfig = {
			tag:'span', cls:'x-form-twin-triggers', cn:[
				{tag: "img", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.trigger1Class},
				{tag: "img", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.trigger2Class}
			]
		};
		
		Ext.sitech.MultiSelect.superclass.initComponent.call(this);
		if (this.multiSelect){
			this.typeAhead = false;
			//this.editable = false;
			//this.lastQuery = this.allQuery;
			this.triggerAction = 'all';
		}
		this.valueArray = this.getValue()?[this.getValue()]:[];
	
	},
	
	hideTrigger1:true,
	
	getTrigger:Ext.form.TwinTriggerField.prototype.getTrigger,
	
	initTrigger:Ext.form.TwinTriggerField.prototype.initTrigger,
	
	trigger1Class:'x-form-clear-trigger',
	
	onTrigger2Click:function(){
		this.onTriggerClick();
	},
	
	onTrigger1Click:function(){
		this.clearValue();
	},
	
	clearValue:function(){
		this.setValue('');
		Ext.sitech.MultiSelect.superclass.clearValue.call(this);
		this.valueArray = [];
		var nodes = Ext.apply([], this.view.getSelectedNodes());
		for (var i=0, len=nodes.length; i<len; i++){
			this.view.deselect(nodes[i], true);
		}
	},
	
	reset:function(){
		if(this.view != undefined ){
			var nodes = Ext.apply([], this.view.getSelectedNodes());
			for (var i=0, len=nodes.length; i<len; i++){
				this.view.deselect(nodes[i], true);
			}
		}
		Ext.sitech.MultiSelect.superclass.reset.call(this);
	},
	
	initList:function(){
        if(!this.list){
            var cls = 'x-combo-list';

            this.list = new Ext.Layer({
                shadow: this.shadow, cls: [cls, this.listClass].join(' '), constrain:false
            });

            var lw = this.listWidth || Math.max(this.wrap.getWidth(), this.minListWidth);
            this.list.setWidth(lw);
            this.list.swallowEvent('mousewheel');
            this.assetHeight = 0;

            if(this.title){
                this.header = this.list.createChild({cls:cls+'-hd', html: this.title});
                this.assetHeight += this.header.getHeight();
            }

            this.innerList = this.list.createChild({cls:cls+'-inner'});
						this.innerList.on('mouseover', this.onViewOver, this);
            this.innerList.on('mousemove', this.onViewMove, this);
            this.innerList.setWidth(lw - this.list.getFrameWidth('lr'))

            if(this.pageSize){
                this.footer = this.list.createChild({cls:cls+'-ft'});
                this.pageTb = new Ext.PagingToolbar({
                    store:this.store,
                    pageSize: this.pageSize,
                    renderTo:this.footer
                });
                this.assetHeight += this.footer.getHeight();
            }

            if(!this.tpl){
                this.tpl = '<tpl for="."><div class="'+cls+'-item">{' + this.displayField + '}</div></tpl>';
            }

		    /**
		    * The {@link Ext.DataView DataView} used to display the ComboBox's options.
		    * @type Ext.DataView
		    */
            this.view = new Ext.DataView({
                applyTo: this.innerList,
                tpl: this.tpl,
                singleSelect: true,
								
								// ANDRIE
								multiSelect: this.multiSelect,
								simpleSelect: true,
								overClass:cls + '-cursor',
								// END
								
                selectedClass: this.selectedClass,
                itemSelector: this.itemSelector || '.' + cls + '-item'
            });

            this.view.on('click', this.onViewClick, this);
						// ANDRIE
						this.view.on('beforeClick', this.onViewBeforeClick, this);
						// END

            this.bindStore(this.store, true);
						
						if (this.valueArray.length){
							this.selectByValue(this.valueArray);
						}

            if(this.resizable){
                this.resizer = new Ext.Resizable(this.list,  {
                   pinned:true, handles:'se'
                });
                this.resizer.on('resize', function(r, w, h){
                    this.maxHeight = h-this.handleHeight-this.list.getFrameWidth('tb')-this.assetHeight;
                    this.listWidth = w;
                    this.innerList.setWidth(w - this.list.getFrameWidth('lr'));
                    this.restrictHeight();
                }, this);
                this[this.pageSize?'footer':'innerList'].setStyle('margin-bottom', this.handleHeight+'px');
            }
        }
        //this.addValue('xt01');
        //this.addValue('j21k');
    },
	
	// private
	onViewOver:function(e, t){
		if(this.inKeyMode){ // prevent key nav and mouse over conflicts
			return;
		}
		// ANDRIE
		/*var item = this.view.findItemFromChild(t);
		if(item){
			var index = this.view.indexOf(item);
			this.select(index, false);
		}*/
		// END
	},
	
	// private
	initEvents:function(){
		Ext.form.ComboBox.superclass.initEvents.call(this);

		this.keyNav = new Ext.KeyNav(this.el, {
			"up" : function(e){
				this.inKeyMode = true;
				this.hoverPrev();
			},

			"down" : function(e){
				if(!this.isExpanded()){
					this.onTriggerClick();
				}else{
					this.inKeyMode = true;
					this.hoverNext();
				}
			},

			"enter" : function(e){
				if (this.isExpanded()){
					this.inKeyMode = true;
					var hoveredIndex = this.view.indexOf(this.view.lastItem);
					this.onViewBeforeClick(this.view, hoveredIndex, this.view.getNode(hoveredIndex), e);
					this.onViewClick(this.view, hoveredIndex, this.view.getNode(hoveredIndex), e);
				}else{
					this.onSingleBlur();
				}
				return true;
			},

			"esc" : function(e){
				this.collapse();
			},

			"tab" : function(e){
				this.collapse();
				return true;
			},
			
			"home" : function(e){
				this.hoverFirst();
				return false;
			},
			
			"end" : function(e){
				this.hoverLast();
				return false;
			},

			scope : this,

			doRelay : function(foo, bar, hname){
				if(hname == 'down' || this.scope.isExpanded()){
				   return Ext.KeyNav.prototype.doRelay.apply(this, arguments);
				}
				// ANDRIE
				if(hname == 'enter' || this.scope.isExpanded()){
				   return Ext.KeyNav.prototype.doRelay.apply(this, arguments);
				}
				// END
				return true;
			},

			forceKeyDown: true
		});
		this.queryDelay = Math.max(this.queryDelay || 10,
				this.mode == 'local' ? 10 : 250);
		this.dqTask = new Ext.util.DelayedTask(this.initQuery, this);
		if(this.typeAhead){
			this.taTask = new Ext.util.DelayedTask(this.onTypeAhead, this);
		}
		if(this.editable !== false){
			this.el.on("keyup", this.onKeyUp, this);
		}
		if(this.forceSelection){
			this.on('blur', this.doForce, this);
		}
		// ANDRIE
		else if(!this.multiSelect){
			this.on('focus', this.onSingleFocus, this);
			this.on('blur', this.onSingleBlur, this);
		}
		this.on('change', this.onChange, this);
		// END
	},
	
	onChange:function(){
		if (!this.clearTrigger){
			return;
		}
		if (this.getValue() != ''){
			this.triggers[0].show();
		}else{
			this.triggers[0].hide();
		}
	},
	
	// private
	selectFirst:function(){
		var ct = this.store.getCount();
		if(ct > 0){
			this.select(0);
		}
	},
	
	// private
	selectLast:function(){
		var ct = this.store.getCount();
		if(ct > 0){
			this.select(ct);
		}
	},
	
	/**
	* Hover an item in the dropdown list by its numeric index in the list.
	* @param {Number} index The zero-based index of the list item to select
	* @param {Boolean} scrollIntoView False to prevent the dropdown list from autoscrolling to display the
	* hovered item if it is not currently in view (defaults to true)
	*/
	hover:function(index, scrollIntoView){
		if (!this.view){
			return;
		}
		this.hoverOut();
		var node = this.view.getNode(index);
		this.view.lastItem = node;
		Ext.fly(node).addClass(this.view.overClass);
		if(scrollIntoView !== false){
			var el = this.view.getNode(index);
			if(el){
				this.innerList.scrollChildIntoView(el, false);
			}
		}
	},
	
	hoverOut:function(){
		if (!this.view){
			return;
		}
		if (this.view.lastItem){
			Ext.fly(this.view.lastItem).removeClass(this.view.overClass);
			delete this.view.lastItem;
		}
	},

	// private
	hoverNext:function(){
		if (!this.view){
			return;
		}
		var ct = this.store.getCount();
		if(ct > 0){
			if(!this.view.lastItem){
				this.hover(0);
			}else{
				var hoveredIndex = this.view.indexOf(this.view.lastItem);
				if(hoveredIndex < ct-1){
					this.hover(hoveredIndex+1);
				}
			}
		}
	},

	// private
	hoverPrev:function(){
		if (!this.view){
			return;
		}
		var ct = this.store.getCount();
		if(ct > 0){
			if(!this.view.lastItem){
				this.hover(0);
			}else{
				var hoveredIndex = this.view.indexOf(this.view.lastItem);
				if(hoveredIndex != 0){
					this.hover(hoveredIndex-1);
				}
			}
		}
	},
	
	// private
	hoverFirst:function(){
		var ct = this.store.getCount();
		if(ct > 0){
			this.hover(0);
		}
	},
	
	// private
	hoverLast:function(){
		var ct = this.store.getCount();
		if(ct > 0){
			this.hover(ct);
		}
	},

	collapse:function(){
		this.hoverOut();
		Ext.sitech.MultiSelect.superclass.collapse.call(this);
	},

	expand:function(){
		Ext.sitech.MultiSelect.superclass.expand.call(this);
	},

	// private
	onSelect:function(record, index){
		//alert('onSelect');
		if(this.fireEvent('beforeselect', this, record, index) !== false){
			this.addValue(record.data[this.valueField || this.displayField]);
			this.fireEvent('select', this, record, index);
			if (!this.multiSelect){
				this.collapse();
			}
		}
	},

	/**
	 * Add a value if this is a multi select
	 * @param {String} value The value to match
	 */
	addValue:function(v){
		v = String(v);
		if (!this.multiSelect){
			this.setValue(v);
			return;
		}
		if (this.valueArray.indexOf(v) == -1){
			this.valueArray.push(v);
			this.setValue(this.valueArray);
		}
	},
	
	/**
	 * Remove a value
	 * @param {String} value The value to match
	 */
	removeValue:function(v){
		v = String(v);
		if (this.list){
			var r = this.findRecord(this.valueField, v);
			this.deselect(this.store.indexOf(r));
		}
		this.valueArray.remove(v);
		this.setValue(this.valueArray);
	},
	
	/**
	 * Sets the specified value for the field. The value can be an Array or a String (optionally with separating commas)
	 * If the value finds a match, the corresponding record text will be displayed in the field.
	 * @param {Mixed} value The value to match
	 */
	setValue:function(v){
		//alert('setValue');
		//alert(v);
		var result = [],
				resultRaw = [];
		if (!(v instanceof Array)){
			//xuyd
			if(v.indexOf(this.delimiter) == -1){
				v = [v];
			}else{
				v = v.split(this.delimiter);
			}
			
		}
		if (!this.multiSelect && v instanceof Array){
			v = v.slice(0,1);
		} 
		for (var i=0, len=v.length; i<len; i++){
			var value = v[i];
			var text = value;
			if(this.valueField){
				var r = this.findRecord(this.valueField, value);
				if(r){
					text = r.data[this.displayField];
				}
			}
			resultRaw.push(value);
			result.push(text);
		}
		v = resultRaw.join(this.delimiter);
		text = result.join(this.delimiter);
		
		this.lastSelectionText = text;
		this.valueArray = resultRaw;
		if(this.hiddenField){
			this.hiddenField.value = v;
		}
		Ext.form.ComboBox.superclass.setValue.call(this, text);
		this.value = v;
		if (this.view){
			//alert('clear');
			this.view.clearSelections();
			this.selectByValue(this.valueArray);
		}
		
		if (this.oldValueArray != this.valueArray){
			this.fireEvent('change', this, this.oldValueArray, this.valueArray);
		}
		this.oldValueArray = Ext.apply([], this.valueArray);
		if (this.history && !this.multiSelect && this.mode == 'local'){
			this.addHistory(this.getRawValue());
		}
	},
	
	// private
	onLoad:function(){
		//alert('load');
		if(!this.hasFocus){
			return;
		}
		if(this.store.getCount() > 0){
			this.expand();
			this.restrictHeight();
			if(this.lastQuery == this.allQuery){
				if(this.editable){
					this.el.dom.select();
				}
				//xuyd
				if(this.value){
					var selectedArr = this.value.split(this.delimiter);
					for(var i = 0; i < selectedArr.length; i++){
						this.selectByValue(selectedArr[i], true);
					}
				}
				//this.preClickSelections = this.view.getSelectedIndexes();
				
				//this.onViewBeforeClick(this.view, hoveredIndex, this.view.getNode(hoveredIndex), e);
				//end
				// ANDRIE

				//this.selectByValue(this.value, true);
				/*if(!this.selectByValue(this.value, true)){
					this.select(0, true);
				}*/
				// END
			}else{
				this.selectNext();
				if(this.typeAhead && this.lastKey != Ext.EventObject.BACKSPACE && this.lastKey != Ext.EventObject.DELETE){
					this.taTask.delay(this.typeAheadDelay);
				}
			}
		}else{
			this.onEmptyResults();
		}
		//this.el.focus();
	},
	
	selectByValue:function(v, scrollIntoView){
		//alert('selectByValue');
		this.hoverOut();
		if(v !== undefined && v !== null){
			if (!(v instanceof Array)){
				v = [v];
			}
			var result = [];
			for (var i=0, len=v.length; i<len; i++){
				var value = v[i];
				var r = this.findRecord(this.valueField || this.displayField, value);
				if(r){
					this.select(this.store.indexOf(r), scrollIntoView);
					//this.view.select(this.store.indexOf(r),true);
					result.push(value);
				}
			}
			return result.join(this.delimiter);
		}
		return false;
	},
	
	// private
	onViewBeforeClick:function(vw, index, node, e){
		//alert("before click")
		this.preClickSelections = this.view.getSelectedIndexes();
		//alert(this.preClickSelections.length);
	},
	
	// private
	onViewClick:function(vw, index, node, e){
	//alert('onViewClick'+this.view.getSelectedIndexes().length);
		if (typeof index != 'undefined'){
			//checked?
			var arrayIndex = this.preClickSelections.indexOf(index);
			//alert(arrayIndex);
			if (arrayIndex != -1 && this.multiSelect){
				//alert('aaa');
				this.removeValue(this.store.getAt(index).data[this.valueField || this.displayField]);
				if (this.inKeyMode){
					this.view.deselect(index, true);
				}
			}else{
				var r = this.store.getAt(index);
				if (r){
					if (this.inKeyMode){
						this.view.select(index, true);
					}
					this.onSelect(r, index);
				}
			}
		}
			
		// from the old doFocus argument; don't really know its use
		if(vw !== false){
			this.el.focus();
		}
	},

	validateValue:function(value){
		if(!Ext.sitech.MultiSelect.superclass.validateValue.call(this, value)){
			return false;
		}
		if (this.valueArray.length < this.minLength){
			this.markInvalid(String.format(this.minLengthText, this.minLength));
			return false;
		}
		if (this.valueArray.length > this.maxLength){
			this.markInvalid(String.format(this.maxLengthText, this.maxLength));
			return false;
		}
		return true;
	},
	
	select:function(index, scrollIntoView){
		//alert('select');
		this.selectedIndex = index;
		if (!this.view){
			return;
		}
		this.view.select(index, this.multiSelect);
		if(scrollIntoView !== false){
			var el = this.view.getNode(index);
			if(el){
				this.innerList.scrollChildIntoView(el, false);
			}
		}
	},
	
	deselect:function(index, scrollIntoView){
	//alert('deselect');
		this.selectedIndex = index;
		this.view.deselect(index, this.multiSelect);
		if(scrollIntoView !== false){
			var el = this.view.getNode(index);
			if(el){
				this.innerList.scrollChildIntoView(el, false);
			}
		}
	},

	// ability to delete value with keyboard
	doForce:function(){
		//alert('doForce');
		if(this.el.dom.value.length > 0){
			if (this.el.dom.value == this.emptyText){
				this.clearValue();
			}
			else{
				this.el.dom.value =
					this.lastSelectionText === undefined?'':this.lastSelectionText;
				this.applyEmptyText();
			}
		}
	},
	
	// private
	onSingleBlur:function(){
		var r = this.findRecord(this.displayField, this.getRawValue());
		if (r){
			this.select(this.store.indexOf(r));
			return;
		}
		if (String(this.oldValue) != String(this.getRawValue())){
			this.setValue(this.getRawValue());
			this.fireEvent('change', this, this.oldValue, this.getRawValue());
		}
		this.oldValue = String(this.getRawValue());
	},
	
	// private
	onSingleFocus:function(){
		this.oldValue = this.getRawValue();
	},
	
	addHistory:function(value){
		if (!value.length){
			return;
		}
		var r = this.findRecord(this.displayField, value);
		if (r){
			this.store.remove(r);
		}else{
			var o = this.store.reader.readRecords([[value]]);
			r = o.records[0];
		}
		this.store.clearFilter();
		this.store.insert(0, r);
		this.pruneHistory();
	},
	
	// private
	pruneHistory:function(){
		if (this.historyMaxLength == 0){
			return;
		}
		if (this.store.getCount()>this.historyMaxLength){
			var overflow = this.store.getRange(this.historyMaxLength, this.store.getCount());
			for (var i=0, len=overflow.length; i<len; i++){
				this.store.remove(overflow[i]);
			}
		}
	}
});
Ext.reg('multiselect', Ext.sitech.MultiSelect);

/**
	className: Ext.sitech.MultiSelectTwin
	xtype: multiselecttwin
	功能描述：多选下拉框 同Ext.sitech.MultiSelect
	右侧增加了关闭图标trigger3Class
 */
Ext.sitech.MultiSelectTwin = Ext.extend( Ext.sitech.MultiSelect, {
		initComponent:function(){
		
		Ext.sitech.MultiSelectTwin.superclass.initComponent.call(this);
		//from twintrigger
		this.triggerConfig = {
			tag:'span', cls:'x-form-twin-triggers', cn:[
				{tag: "img", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.trigger1Class},
				{tag: "img", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.trigger2Class},
				{tag: "img", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.trigger3Class}
			]
		};
		

	},
	trigger3Class:'x-form-clear-trigger',
	onTrigger3Click : Ext.emptyFn
});
Ext.reg('multiselecttwin', Ext.sitech.MultiSelectTwin);