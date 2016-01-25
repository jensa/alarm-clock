var React = require('react');
var ReactDOM = require('react-dom');

var Post = React.createClass({

  getInitialState: function(){
    return {};
  },

  render: function(){
    return (<div>{this.props.title}</div>)
  }
})

var LatestPostList = React.createClass({

  getInitialState: function() {
    return {
      list: []
    };
  },

  componentDidMount: function() {
    $.get(this.props.source, function(result) {
      var postList = result;
      if (this.isMounted()) {
        this.setState({
          list:postList
        });
      }
    }.bind(this));
  },

  render: function(){
    var createItem = function(item) {
      return <Post title={item.title} key={item.id}/>;
    };

    var posts = this.state.list.map(createItem)
    return (<div>{posts}</div>);
  }
})

ReactDOM.render(
  <LatestPostList source="http://localhost:3000/posts/popular"/>,
  document.getElementById('content')
);
