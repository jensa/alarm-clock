var React = require('react');
var ReactDOM = require('react-dom');

var SoundLink = React.createClass({
  render: function(){
    return(<a href={this.props.link}>{this.props.title}</a>)
  }
})

var Post = React.createClass({

  getInitialState: function(){
    return {};
  },

  render: function(){
    return (<div><SoundLink title={this.props.title} link={"http://localhost:3000/data?id=" + this.props.stuff}/></div>)
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
      return <Post title={item.title} key={item.id} time={item.time} stuff={item.id}/>;
    };

    var posts = this.state.list.map(createItem)
    return (<div>{posts}</div>);
  }
})

ReactDOM.render(
  <LatestPostList source="http://localhost:3000/posts/popular"/>,
  document.getElementById('content')
);
