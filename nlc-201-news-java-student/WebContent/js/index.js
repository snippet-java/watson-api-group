/**
 * 
 */
$(document).ready(function() {
	
	$('#feedback').click(function(){
		var $watson = $('.watson'),
		$output = $('.output'),
		$outputSimple = $('.outputSimple'),
		queryOutput = JSON.parse($('.results--item-details').text()),
		feedback = '{"textNews" : "'+queryOutput.text+'","classReturned" : "'+queryOutput.top_class+'","classSuggested" : "'+$('#class').val()+'"}';
		
		//alert(feedback);
		$outputSimple.hide();
		$output.hide();
		$watson.show();
		
		$.ajax({
			url: 'api/news/feedback',
            type: "POST",
            data: '{"textNews" : "'+queryOutput.text+'","classReturned" : "'+queryOutput.top_class+'","classSuggested" : "'+$('#class').val()+'"}',
            contentType: 'application/json',
            success: function(data, textStatus, jqXHR){
            	$('#title_message').html('Message');
            	$('#message').html('Response : '+ data);
                $watson.hide();
                $output.show();
                $outputSimple.show();
            },
            error: function(jqXHR, textStatus, errorThrown){
            	$('#title_message').html('Error');
            	$('#message').html('Feedback Error : '+ jqXHR.responseText);
            	$watson.hide();
            	$output.show();
            	$outputSimple.show();
            }
          });
	
	});	
	
	$('#classifyText').click(function(){

		// jQuery nodes
		var $serviceResults = $('.service--results'),
		$template = $('.result--template'),
		$output = $('.output'),
		$error = $('.outputSimple'),
		$watson = $('.watson'),
		query = $('#textNews').val();
		
		query = query.replace(/"/g , "'");
		query = query.replace(/\n/g,"\\n");
		query = query.replace(/\t/g,"\\t");
		query = query.replace(/\r/g,"\\r");
		query = query.substring(0,1024);		
		//alert("Query modifiedL "+query);
		
		$error.hide();
		$output.hide();
		$watson.show();
		
		//alert('Button Clicked 2');
		$.ajax({
			url: 'api/news/classify',
            type: "POST",
            data: '{"textNews" : "'+query+'"}',
            contentType: 'application/json',
            success: function(data, textStatus, jqXHR){
            	 $serviceResults.empty();
            	 
            	 var node = $template.last().clone().show();

                 node.find('.results--item-text').prepend('Top Class : '+data.top_class);
                 node.find('.results--item-details').html(JSONTree.create(data)); 
                 
                 var $moreInfo = node.find('.results--more-info');
                 node.find('.results--see-more').click(function() {
                   if ($moreInfo.css('display') === 'none')
                     $moreInfo.fadeIn('slow');
                   else
                     $moreInfo.fadeOut(500);
                 });
                 
                 addFeedbackClassification(node);
                 
                 $serviceResults.append(node); 
                 
                 $watson.hide();
                 $output.show();
            },
            error: function(jqXHR, textStatus, errorThrown){
            	$('#title_message').html('Error');
            	$('#message').html('Classify Error : '+ jqXHR.responseText);
            	$watson.hide();
            	$error.show();
            }
          });
	});	
	
	function addFeedbackClassification(node){ 	  
  		var selectHTML = "<div class=\"results--item-text\">Suggest other classification : ";
  		selectHTML+="<select id=\"class\">";
  		var choices = ["Business", "Entertainment","Politics","Sport","Technology"];
  		var valChoices = ["business", "entertainment","politics","sport","technology"];
  		for(var i=0; i < choices.length; i = i + 1) 
  			selectHTML += "<option value='" + valChoices[i] + "'>" + choices[i] + "</option>";
  		selectHTML += "</select></div>";
  		node.find('.results--item-change-class').html(selectHTML);
      }   
});